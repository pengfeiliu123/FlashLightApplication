package com.lpf.flashlight;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lpf.flashlight.colorpicker.ColorPickerDialog;
import com.lpf.flashlight.colorpicker.ColorPickerSwatch;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends FragmentActivity {
    private final Flash flash = new Flash();
    private LinearLayout background;
    private ToggleButton theButton;

    private SeekBar lightSeekBar;
    private ImageView colorChoose;
    private LinearLayout flashLightLayout;

    private float lastDistance = 0;
    private float currentDistance = 0;
    private float scaleSize = 1f;

    private CircleImageView sosImg;
    private Thread mThread;
    private boolean isSosOn = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Fabric.with(this);

        initViews();

        initSeekBar();

        initBackground();

        initColorPicker();

        initSos();



    }

    private void initViews() {
        theButton = (ToggleButton) findViewById(R.id.flashlightButton);
        background = (LinearLayout) findViewById(R.id.background);
        background.setOnLongClickListener(new LongClickListener());

        flashLightLayout = (LinearLayout) findViewById(R.id.flashLightLayout);
    }

    private void initSos() {
        sosImg = (CircleImageView) findViewById(R.id.sos);
        sosImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSosOn = !isSosOn;
                if (isSosOn) {
                    openSos();
                    sosOnUI(ContextCompat.getColor(MainActivity.this, R.color.white), R.mipmap.sos_red);
                } else {
                    closeSos();
                    sosOnUI(ContextCompat.getColor(MainActivity.this, R.color.red), R.mipmap.sos_white);
                }
            }
        });

        sosImg.setVisibility(View.VISIBLE);
        sosOnUI(ContextCompat.getColor(MainActivity.this, R.color.red), R.mipmap.sos_white);
    }

    private void initColorPicker() {
        final ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
        int[] colors = getResources().getIntArray(R.array.color_choose);
        colorPickerDialog.initialize(R.string.title, colors, colors[0], 4, 2);
        colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                background.setBackgroundColor(color);
                PreferencesUtil.getInstance(MainActivity.this).savedBgColor(color);
            }
        });

        colorChoose = (ImageView) findViewById(R.id.color_choose);
        colorChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPickerDialog.show(getSupportFragmentManager(), "colorpicker");
            }
        });
    }

    private void initBackground() {
        background.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //更兼容的方式
                int actionMasked = MotionEventCompat.getActionMasked(event);

                switch (actionMasked) {

                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //当触控点多于两个时进行缩放操作
                        if (event.getPointerCount() >= 2) {
                            //设置当前的两点间的距离
                            setCurrentDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) flashLightLayout.getLayoutParams();
                            //获取用户屏幕的宽度
                            Point point = new Point();
                            ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point);
                            int screenSize = point.x;
                            //当当前距离大于之前距离时，进行放大操作
                            if (lastDistance <= 0) {
                                lastDistance = currentDistance;
                            } else {
                                if (currentDistance > lastDistance) {
                                    if (flashLightLayout.getWidth() < screenSize / 2) {
                                        scaleSize = 1.05f;
                                    } else {
                                        scaleSize = 1f;
                                    }
                                    //设置完放大参数后，将当前距离赋值给lastDistance
                                    lastDistance = currentDistance;
                                    //当当前距离小于之前距离时，进行缩小操作
                                } else if (currentDistance < lastDistance) {
                                    //防止图片被缩小的太小，最小不能超过屏幕宽度的1/4
                                    if (flashLightLayout.getWidth() > screenSize / 4) {
                                        scaleSize = 0.95f;
                                    } else {
                                        scaleSize = 1f;
                                    }
                                    lastDistance = currentDistance;
                                }
                                //设置layoutParams进行缩放操作
                                layoutParams.height = (int) (flashLightLayout.getHeight() * scaleSize);
                                layoutParams.width = (int) (flashLightLayout.getWidth() * scaleSize);
                                flashLightLayout.setLayoutParams(layoutParams);
                            }
                        }
                        break;

                    //触摸动作抬起时将两个距离初始化
                    case MotionEvent.ACTION_UP:
                        lastDistance = 0;
                        break;
                }
                return true;
            }
        });
    }

    private void initSeekBar() {
        lightSeekBar = (SeekBar) findViewById(R.id.light_seekbar);
        lightSeekBar.setMax(254);
        lightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setWindowBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void sosOnUI(int color, int sos_red) {
        sosImg.setFillColor(color);
        sosImg.setBorderColor(color);
        sosImg.setImageResource(sos_red);
    }

    private void closeSos() {
        if (mThread != null) {
            mThread.interrupt();
        }
    }

    private void openSos() {
        if (mThread != null) {
            mThread.interrupt();
        }
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isSosOn) {

                        for (int i = 0; i < 3; i++) {
                            Thread.sleep(250);
                            flash.off();
                            Thread.sleep(250);
                            flash.on();
                        }

                        for (int i = 0; i < 3; i++) {
                            Thread.sleep(500);
                            flash.off();
                            Thread.sleep(500);
                            flash.on();
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mThread.start();

    }

    private float setCurrentDistance(float x1, float y1, float x2, float y2) {
        currentDistance = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        return currentDistance;
    }

    @Override
    public void onResume() {
        super.onResume();

        int savedColor = PreferencesUtil.getInstance(this).getBgColor();
        background.setBackgroundColor(savedColor);

        if (theButton.isChecked()) {
            theButton.setEnabled(false);
            new FlashTask().execute();
            theButton.setKeepScreenOn(true);

            sosImg.setVisibility(View.VISIBLE);
            sosOnUI(ContextCompat.getColor(MainActivity.this, R.color.red), R.mipmap.sos_white);

        } else {
            flash.off();
            sosImg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        flash.off();
        if (mThread != null) {
            mThread.interrupt();
        }
        isSosOn = false;
        sosImg.setVisibility(View.GONE);
        flash.close();
    }

    public void onToggleClicked(View v) {
        if (theButton.isChecked()) {
            new FlashTask().execute();
            v.setKeepScreenOn(true);
            theButton.setChecked(true);
            isSosOn = false;
            sosImg.setVisibility(View.VISIBLE);
            sosOnUI(ContextCompat.getColor(MainActivity.this, R.color.red), R.mipmap.sos_white);
        } else {
            sosImg.setVisibility(View.GONE);
            flash.off();
            v.setKeepScreenOn(false);
            theButton.setChecked(false);

            if (mThread != null) {
                mThread.interrupt();
            }
        }
    }

    public class LongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            theButton.setChecked(!theButton.isChecked());
            onToggleClicked(theButton);
            return true;
        }
    }

    public class FlashTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            return flash.on();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            theButton.setEnabled(true);
            if (!success) {
                Toast.makeText(MainActivity.this, "Failed to access camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setWindowBrightness(int brightness) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness / 255.0f;
        window.setAttributes(lp);
    }
}
