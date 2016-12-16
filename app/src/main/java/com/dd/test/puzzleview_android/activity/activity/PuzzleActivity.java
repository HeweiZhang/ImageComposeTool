package com.dd.test.puzzleview_android.activity.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dd.test.puzzleview_android.R;
import com.dd.test.puzzleview_android.activity.dialog.TemplateDialog;
import com.dd.test.puzzleview_android.activity.entity.ImageBean;
import com.dd.test.puzzleview_android.activity.entity.Puzzle;
import com.dd.test.puzzleview_android.activity.util.DensityUtil;
import com.dd.test.puzzleview_android.activity.util.FileUtil;
import com.dd.test.puzzleview_android.activity.view.PuzzleView;
import com.dd.test.puzzleview_android.activity.view.TopView;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by dd on 16/1/13.
 * 拼图主界面
 */
public class PuzzleActivity extends Activity implements View.OnClickListener {

    private Context context;
    private TopView topView;
    private LinearLayout puzzleLL;
    private PuzzleView puzzleView;
    private TextView templateTv;
    private List<ImageBean> imageBeans;
    private Puzzle puzzleEntity;
    private TemplateDialog templateDialog;
    private String pathFileName;
    private int lastSelect = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        init();
    }

    private void init() {

        context = PuzzleActivity.this;
        initView();
        initData();
        initEvent();
    }

    private void initView() {

        topView = (TopView) findViewById(R.id.top_view);
        puzzleLL = (LinearLayout) findViewById(R.id.puzzle_ll);
        puzzleView = (PuzzleView) findViewById(R.id.puzzle_view);
        templateTv = (TextView) findViewById(R.id.template_tv);
    }

    private void initData() {

        imageBeans = (List<ImageBean>) getIntent().getSerializableExtra("pics");
        getFileName(imageBeans.size());
        templateDialog = new TemplateDialog(context, imageBeans.size());//选择模板dialog
        topView.setTitle("拼图");
        topView.setRightWord("保存");
        puzzleView.setPics(imageBeans);//绘制视图
        if (pathFileName != null) {
            initCoordinateData(pathFileName, 0);
        }
    }

    private void initEvent() {
        templateTv.setOnClickListener(this);

        topView.setOnLeftClickListener(new TopView.OnLeftClickListener() {
            @Override
            public void leftClick() {
                finish();
            }
        });
        topView.setOnRightClickListener(new TopView.OnRightClickListener() {
            @Override
            public void rightClick() {
                savePuzzle();

            }
        });

        templateDialog.setOnItemClickListener(new TemplateDialog.OnItemClickListener() {
            @Override
            public void OnItemListener(int position) {
                if (position != lastSelect) {
                    initCoordinateData(pathFileName, position);
                    puzzleView.invalidate();
                    lastSelect = position;
                }
                templateDialog.dismiss();
            }
        });
    }

    private void getFileName(int picNum) {

        switch (picNum) {

            case 2:
                pathFileName = "num_two_style";
                break;
            case 3:
                pathFileName = "num_three_style";
                break;
            case 4:
                pathFileName = "num_four_style";
                break;
            case 5:
                pathFileName = "num_five_style";
                break;
            case 8:
                pathFileName = "num_eight_style";
                break;
            default:
                break;
        }
    }

    //获取坐标信息
    private void initCoordinateData(String fileName, int templateNum) {

        String data = new FileUtil(context).readAsset(fileName);
        try {
            Gson gson = new Gson();
            puzzleEntity = gson.fromJson(data, Puzzle.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (puzzleEntity != null && puzzleEntity.getStyle() != null && puzzleEntity.getStyle().get(templateNum).getPic() != null) {
            puzzleView.setPathCoordinate(puzzleEntity.getStyle().get(templateNum).getPic());
        }

    }

    private void savePuzzle() {

        /**
         * 将view转化为Bitmap
         */
        buildDrawingCache(puzzleLL);
        puzzleLL.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        Bitmap bitmap = puzzleLL.getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);

        try {
            FileUtil.saveBitmap(bitmap, "/sdcard/compose/", "new_" + System.currentTimeMillis() + ".jpg");//保存放大后的图
            FileUtil.saveBitmap(saveBitBitmap(), "/sdcard/compose/", "new_Big_" + System.currentTimeMillis() + ".jpg");//保存放大后的图

            File file = FileUtil.saveBitmapJPG(context, "dd" + System.currentTimeMillis(), bitmap);//预览原图
            Intent intent = new Intent("puzzle");
            intent.putExtra("picPath", file.getPath());
            sendBroadcast(intent);
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap saveBitBitmap() {
        Bitmap[] bitmapSources = puzzleView.getBitmaps();
        float[][] bitmapPos = puzzleView.getBitmapPos();

        //创建一个新的指定长度宽度一样的位图
        Bitmap newb = Bitmap.createBitmap(3508, 4961, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newb);
        cv.drawColor(Color.WHITE);
        Matrix matrix = new Matrix();
        float scaleSize = 3508 / dp2px(350);
        Log.e("info", "转为大图切换比例：" + scaleSize);

        matrix.postScale(scaleSize, scaleSize); //长和宽放大缩小的比例
        for (int i = 0; i < bitmapPos.length; i++) {
            cv.drawBitmap(Bitmap.createBitmap(bitmapSources[i], 0, 0, bitmapSources[i].getWidth(), bitmapSources[i].getHeight(), matrix, true), dp2px(bitmapPos[i][0]) * scaleSize, dp2px(bitmapPos[i][1]) * scaleSize, new Paint());//在 0，0坐标开始画入bitmap1
        }
        cv.save(Canvas.ALL_SAVE_FLAG);//保存
        return newb;
    }

    private int dp2px(float point) {
        return DensityUtil.dip2px(this, point);
    }

    /**
     * 配置cache值
     *
     * @param view
     */

    private void buildDrawingCache(View view) {
        try {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
    }


    /**
     * Bitmap放大的方法
     */
    private Bitmap magnifyBimap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        float scaleSize;
        scaleSize = 4961 / bitmap.getHeight();
        matrix.postScale(scaleSize, scaleSize); //长和宽放大缩小的比例
        Log.e("info", "scaleSize:" + scaleSize);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return resizeBmp;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.template_tv:
                templateDialog.show();
                break;

            default:
                break;
        }
    }

}
