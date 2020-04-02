package com.pomelo.schoolar.scan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.pomelo.schoolar.R;
import com.pomelo.schoolar.base.BaseMVPActivity;
import com.pomelo.schoolar.model.ModelActivity;
import com.pomelo.schoolar.utils.DownLoadUtils;
import com.pomelo.schoolar.utils.ScreenUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jsc.kit.cameramask.ScannerBarView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by wanghaoxiang on 2020-03-05.
 */

public class ScanARActivity extends BaseMVPActivity<ScanARPresenter> implements ScanARView, DownLoadUtils.DownLoadInterface {
    Camera camera;
    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.btn)
    Button btn;
    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.scanner_view)
    ScannerBarView scannerBarView;
    private SurfaceHolder mHolder;
    String url, fileName;
    DownLoadUtils downLoadUtils;

    /**
     * 预览相机
     */
    private void startPreview(Camera camera, SurfaceHolder holder) {
        try {
            // 确认相机预览尺寸
//            setupCamera(camera);
            camera.setPreviewDisplay(holder);
            setupCamera(camera);
            setCameraDisplayOrientation(this, 0, camera);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCameraDisplayOrientation(Activity activity,
                                            int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        //设置角度
        camera.setDisplayOrientation(result);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_scanner;
    }

    @Override
    public void initView() {
        downLoadUtils = new DownLoadUtils();
        downLoadUtils.setDownLoadInterface(this);
        camera = Camera.open();//默认打开后置相机
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                startPreview(camera, surfaceHolder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                startPreview(camera, surfaceHolder);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
    }

    /**
     * 设置surfaceView的尺寸 因为camera默认是横屏，所以取得支持尺寸也都是横屏的尺寸
     * 我们在startPreview方法里面把它矫正了过来，但是这里我们设置设置surfaceView的尺寸的时候要注意 previewSize.height<previewSize.width
     * previewSize.width才是surfaceView的高度
     * 一般相机都是屏幕的宽度 这里设置为屏幕宽度 高度自适应 你也可以设置自己想要的大小
     */
    private void setupCamera(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        //根据屏幕尺寸获取最佳 大小
        Camera.Size previewSize = getPicPreviewSize(parameters.getSupportedPreviewSizes(), ScreenUtil.dp2px(mContext, 400), ScreenUtil.dp2px(mContext, 400));
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        Camera.Size pictrueSize = getPicPreviewSize(parameters.getSupportedPictureSizes(),
                ScreenUtil.dp2px(mContext, 400), ScreenUtil.dp2px(mContext, 400));
        parameters.setPictureSize(pictrueSize.width, pictrueSize.height);
        camera.setParameters(parameters);
//        picHeight = (screenWidth * pictrueSize.width) / pictrueSize.height;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ScreenUtil.dp2px(mContext, 400),
                (ScreenUtil.dp2px(mContext, 400) * pictrueSize.width) / pictrueSize.height);
        surfaceView.setLayoutParams(params);
    }

    public Camera.Size getPicPreviewSize(List<Camera.Size> list, int th, int minWidth) {
//        Collections.sort(list, ascendSizeComparator);

        int i = 0;
        for (int x = 0; x < list.size(); x++) {
            Camera.Size s = list.get(x);
            // camera 中的宽度和高度 相反 因为测试板子原因 这里暂时 替换 && 为 ||
            if ((s.width == th) && (s.height == minWidth)) {
                i = x;
                break;
            }
        }
        //如果没找到，就选最小的size 0
        return list.get(i);
    }

    @Override
    protected ScanARPresenter createPresenter() {
        return new ScanARPresenter(this);
    }

    @OnClick({R.id.btn, R.id.btn_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn: {
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes, Camera camera) {
                        camera.stopPreview();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        File file = compressByQuality(bitmap, 60, 500);
                        Map<String, RequestBody> bodyMap = new HashMap<>();
                        bodyMap.put("file\";filename=\"" + file.getName(), RequestBody.create(MediaType.parse("image/png"), file));
                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
                        MultipartBody.Part mulFile = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                        mPresenter.scan(mulFile);
                        scannerBarView.setVisibility(View.VISIBLE);
                        scannerBarView.start();
                        btnClose.setVisibility(View.VISIBLE);
                        btn.setVisibility(View.GONE);
                    }
                });
            }
            break;
            case R.id.btn_close: {
                scannerBarView.stop();
                scannerBarView.setVisibility(View.GONE);
                btn.setVisibility(View.VISIBLE);
                btnClose.setVisibility(View.GONE);
                camera.startPreview();
                mPresenter.detachView();
            }
            break;
        }

    }

    public static File compressByQuality(Bitmap bm, int quality, int maxSize) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, bos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        long length = bos.toByteArray().length;
        while (length / 1024 > maxSize) { // 循环判断如果压缩后图片是否大于minSize,大于继续压缩
            bos.reset();// 重置bos即清空bos
            quality -= 5;// 每次都减少5
            bm.compress(Bitmap.CompressFormat.JPEG, quality, bos);// 这里压缩options%，把压缩后的数据存放到baos中
            length = bos.toByteArray().length;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String filename = format.format(date);
        File file = new File(Environment.getExternalStorageDirectory(), filename + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(bos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        recycleBitmap(bm);
        return file;
    }

    public static void recycleBitmap(Bitmap... bitmaps) {
        if (bitmaps == null) {
            return;
        }
        for (Bitmap bm : bitmaps) {
            if (null != bm && !bm.isRecycled()) {
                bm.recycle();
            }
        }
    }

    @Override
    public void uploadSuccess(String url) {
        btnClose.setVisibility(View.GONE);
        btn.setVisibility(View.VISIBLE);
        fileName = "/sdcard/3dmodel/" + url.substring(url.lastIndexOf("/") - 1);
        downLoadUtils.downLoadFile(url);
    }


    @Override
    public void downLoadSuccess(Response<ResponseBody> response) {
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                mPresenter.writeFileFromIS(new File(fileName), response.body().byteStream(), response.body().contentLength());
                emitter.onNext("发送");
                emitter.onComplete();

            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                scannerBarView.setVisibility(View.GONE);
                scannerBarView.stop();
                Intent intent = new Intent(mContext, ModelActivity.class);
                intent.putExtra("fileName", fileName);
                startActivity(intent);
            }
        });
    }
}

