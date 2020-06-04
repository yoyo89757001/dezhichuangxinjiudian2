package megvii.testfacepass.pa.ui;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.Tag;

import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hwit.HwitManager;
import com.lztek.toolkit.Lztek;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.sdsmdg.tastytoast.TastyToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.query.LazyList;
import mcv.facepass.FacePassException;
import mcv.facepass.FacePassHandler;
import mcv.facepass.types.FacePassAddFaceResult;
import mcv.facepass.types.FacePassCompareResult;
import mcv.facepass.types.FacePassDetectionResult;
import mcv.facepass.types.FacePassFace;
import mcv.facepass.types.FacePassImage;
import mcv.facepass.types.FacePassImageType;
import mcv.facepass.types.FacePassMouthOccAttr;
import mcv.facepass.types.FacePassRecognitionResult;
import mcv.facepass.types.FacePassRecognitionResultType;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.R;
import megvii.testfacepass.pa.beans.AddFacesBean;
import megvii.testfacepass.pa.beans.BaoCunBean;
import megvii.testfacepass.pa.beans.FailedPersonBean;
import megvii.testfacepass.pa.beans.HuiFuBean;
import megvii.testfacepass.pa.beans.IDCardBean;
import megvii.testfacepass.pa.beans.IDCardBean_;
import megvii.testfacepass.pa.beans.IDCardTakeBean;
import megvii.testfacepass.pa.beans.IcCardInfosBean;
import megvii.testfacepass.pa.beans.LogingBean;
import megvii.testfacepass.pa.beans.OpenDoorBean;
import megvii.testfacepass.pa.beans.Subject;
import megvii.testfacepass.pa.beans.Subject_;
import megvii.testfacepass.pa.beans.TimeStateBean;
import megvii.testfacepass.pa.beans.XGBean;
import megvii.testfacepass.pa.beans.ZhiLingBean;
import megvii.testfacepass.pa.camera.CameraManager;
import megvii.testfacepass.pa.camera.CameraPreview;

import megvii.testfacepass.pa.camera.CameraPreviewData;
import megvii.testfacepass.pa.dialog.MiMaDialog3;
import megvii.testfacepass.pa.dialog.MiMaDialog4;
import megvii.testfacepass.pa.utils.BitmapUtil;
import megvii.testfacepass.pa.utils.DateUtils;
import megvii.testfacepass.pa.utils.DengUT;
import megvii.testfacepass.pa.utils.FacePassUtil;

import megvii.testfacepass.pa.utils.FileUtil;
import megvii.testfacepass.pa.utils.GetCpuState;
import megvii.testfacepass.pa.utils.GsonUtil;
import megvii.testfacepass.pa.utils.NV21ToBitmap;
import megvii.testfacepass.pa.utils.ScanGunKeyEventHelper;
import megvii.testfacepass.pa.utils.SettingVar;
import megvii.testfacepass.pa.view.GlideRoundTransform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import top.zibin.luban.Luban;


public class MianBanJiActivity3 extends Activity implements CameraManager.CameraListener, ScanGunKeyEventHelper.OnScanSuccessListener {

    @BindView(R.id.xiping)
    ImageView xiping;
    @BindView(R.id.tishiyu)
    TextView tishiyu;
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;
    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.faceLinearLayout)
    LinearLayout faceLinearLayout;
    @BindView(R.id.faceName)
    TextView faceName;
    @BindView(R.id.faceImage)
    ImageView faceImage;



    private NetWorkStateReceiver netWorkStateReceiver = null;
   // private SensorManager sm;
    private Box<Subject> subjectBox = null;
   // private NfcAdapter mNfcAdapter;
  //  private PendingIntent mPendingIntent;
    private Box<IDCardBean> idCardBeanBox = MyApplication.myApplication.getIdCardBeanBox();
   // private ServerManager serverManager;
   // private Bitmap msrBitmap = null;
    private RequestOptions myOptions = new RequestOptions()
            .fitCenter()
            .error(R.drawable.erroy_bg)
           // .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
    .transform(new GlideRoundTransform(MianBanJiActivity3.this,10));
//
//    private RequestOptions myOptions2 = new RequestOptions()
//            .fitCenter()
//            .error(R.drawable.erroy_bg)
//            //   .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
//            .transform(new GlideCircleTransform270(MyApplication.myApplication, 2, Color.parseColor("#ffffffff"), 270));
   // private String serialnumber = GetDeviceId.getDeviceId(MyApplication.myApplication);

    private OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .writeTimeout(8000, TimeUnit.MILLISECONDS)
            .connectTimeout(8000, TimeUnit.MILLISECONDS)
            .readTimeout(8000, TimeUnit.MILLISECONDS)
//				    .cookieJar(new CookiesManager())
            //        .retryOnConnectionFailure(true)
            .build();
    private final Timer timer = new Timer();
    private TimerTask task;
    private final Timer timer2 = new Timer();
    private TimerTask task2;
    private LinkedBlockingQueue<ZhiLingBean> linkedBlockingQueue;
    /* 相机实例 */
    private CameraManager manager;
  //  private CameraManager2 manager2;
    /* 显示人脸位置角度信息 */
    // private XiuGaiGaoKuanDialog dialog = null;
    /* 相机预览界面 */
    private CameraPreview cameraView;
  //  private CameraPreview2 cameraView2;
    private static final int cameraWidth = 720;
    private static final int cameraHeight = 640;
  //  private boolean isOP = true;
    private int heightPixels;
    private int widthPixels;
    int screenState = 0;// 0 横 1 竖
    TanChuangThread tanChuangThread;
    // private ConcurrentHashMap<Long, Integer> concurrentHashMap = new ConcurrentHashMap<Long, Integer>();
    private int dw, dh;
    private ConcurrentHashMap<Long, Integer> concurrentHashMap = new ConcurrentHashMap<Long, Integer>();
    private Box<BaoCunBean> baoCunBeanDao = null;
    private Box<HuiFuBean> huiFuBeanBox = null;
    //private Box<DaKaBean> daKaBeanBox = null;
    private BaoCunBean baoCunBean = null;
    private TimeChangeReceiver timeChangeReceiver;
    private Handler mHandler;
    private static boolean isLink = false;//默认不是刷卡模式
    private FacePassHandler paAccessControl=null;
 //   private Float mCompareThres;
  //  private static String faceId = "";
   // private long feature2 = -1;
    private NV21ToBitmap nv21ToBitmap;
    private SoundPool soundPool;
    //定义一个HashMap用于存放音频流的ID
    private HashMap<Integer, Integer> musicId = new HashMap<>();
    //private int pp = 0;
  //  private ReadThread mReadThread;
   // private ReadThread2 mReadThread2;
    private ReadThread3 mReadThread3;
    //private DogThread dogThread;
    //private InputStream mInputStream;
   // private int w, h, cameraH, cameraW;
   // private float s1 = 0, s2 = 0;
    private Timer mTimer;//距离感应
    private TimerTask mTimerTask;//距离感应
    private int pm = 0;
    //private boolean onP1 = true, onP2 = true;
   // private boolean isPM = true;
  //  private boolean isPM2 = true;
   // private float juli = 0;
    private String JHM = null;
    TextView tvTitle_Ir;
    TextView tvName_Ir;//识别结果弹出信息的名字
    TextView tvTime_Ir;//识别结果弹出信息的时间
    TextView tvFaceTips_Ir;//识别信息提示
    LinearLayout layout_loadbg_Ir;//识别提示大框
    RelativeLayout layout_true_gif_Ir, layout_error_gif_Ir;//蓝色图片动画 红色图片动画
    ImageView iv_true_gif_in_Ir, iv_true_gif_out_Ir, iv_error_gif_in_Ir, iv_error_gif_out_Ir;//定义旋转的动画
    Animation gifClockwise, gifAntiClockwise;
    LinearInterpolator lir_gif;
    private Box<IDCardTakeBean> idCardTakeBeanBox=MyApplication.myApplication.getIdCardTakeBeanBox();
    private int jiqiType=-1;
  //  private boolean isGET = true;
    private int cishu=4;
    private int jidianqi=6000;
    private Lztek lztek=null;
    private Bitmap cardBitmap=null;
   // private Function mFuncs = null;
   // private int loc_readerHandle=-1;
    private boolean isCLOSDLED =false;
  //  private CameraPreviewData mCurrentImage;
    ArrayBlockingQueue<FacePassDetectionResult> mDetectResultQueue;
    ArrayBlockingQueue<FacePassImage> mFeedFrameQueue;
    RecognizeThread mRecognizeThread;
    FeedFrameThread mFeedFrameThread;
    private String group_name = "facepasstestx";
    private int STATE=1;
    private LogingBean logingBean=null;
    private Connection conn=null;
    private Channel channel=null;
    private ConnectionFactory factory=null;
    private String getPerosnPath=null;
    private String perosnNotifyPath=null;
    private int timeUpdataState=1;
    private ScanGunKeyEventHelper mScanGunKeyEventHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        huiFuBeanBox = MyApplication.myApplication.getHuiFuBeanBox();
        baoCunBeanDao = MyApplication.myApplication.getBaoCunBeanBox();
      //  daKaBeanBox=MyApplication.myApplication.getDaKaBeanBox();
        baoCunBean = baoCunBeanDao.get(123456L);
        mDetectResultQueue = new ArrayBlockingQueue<FacePassDetectionResult>(5);
        mFeedFrameQueue = new ArrayBlockingQueue<FacePassImage>(1);
        MyApplication.myApplication.addActivity(this);
        try {
            lztek=Lztek.create(MyApplication.myApplication);
            lztek.gpioEnable(218);
            lztek.setGpioOutputMode(218);
        }catch (NoClassDefFoundError error){
            error.printStackTrace();
        }

        if (baoCunBean.getDangqianChengShi2()!=null){
            switch (baoCunBean.getDangqianChengShi2()){
                case "智连":
                    jiqiType=0;

                    break;
                case "亮钻":
                    jiqiType=1;

                    break;

            }
        }

        JHM = baoCunBean.getJihuoma();
        if (JHM == null)
            JHM = "";
        subjectBox = MyApplication.myApplication.getSubjectBox();
        mScanGunKeyEventHelper=new ScanGunKeyEventHelper(this);
        //每分钟的广播
        // private TodayBean todayBean = null;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//设置了系统时区
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);//设置了系统时间
        timeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(timeChangeReceiver, intentFilter);
        linkedBlockingQueue = new LinkedBlockingQueue<>(1);
        EventBus.getDefault().register(this);//订阅
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        dw = dm.widthPixels;
        dh = dm.heightPixels;
        nv21ToBitmap = new NV21ToBitmap(MianBanJiActivity3.this);
        /* 初始化界面 */
        //  Log.d("MianBanJiActivity3", "jh:" + baoCunBean);
        //初始化soundPool,设置可容纳12个音频流，音频流的质量为5，
        AudioAttributes abs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)   //设置允许同时播放的流的最大值
                .setAudioAttributes(abs)   //完全可以设置为null
                .build();
        //通过load方法加载指定音频流，并将返回的音频ID放入musicId中
        musicId.put(1, soundPool.load(this, R.raw.nihaoqingtongguo, 1));//请通过
        musicId.put(2, soundPool.load(this, R.raw.bunengjinru, 1));//不能进入
        musicId.put(3, soundPool.load(this, R.raw.qingshualian, 1));//请刷脸
        musicId.put(4, soundPool.load(this, R.raw.wangluobutong, 1));//网络不通
        musicId.put(5, soundPool.load(this, R.raw.shujuyichang, 1));//数据异常
        //sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        initView();

//        serverManager = new ServerManager(FileUtil.getIPAddress(getApplicationContext()), baoCunBean.getPort());
//        serverManager.setMyServeInterface(MianBanJiActivity3.this);
//        serverManager.startServer();

        if (baoCunBean != null) {
            try {
                if (baoCunBean.getJidianqi()!=0){
                    jidianqi=baoCunBean.getJidianqi();
                }
                if (baoCunBean.getMoshengrenPanDing()!=0){
                    cishu=baoCunBean.getMoshengrenPanDing();
                }

            //    FacePassHandler.initSDK(getApplicationContext());
             //   FacePassUtil util = new FacePassUtil();
             //   util.init(MianBanJiActivity3.this, getApplicationContext(), SettingVar.faceRotation, baoCunBean);

            } catch (Exception e) {
               TastyToast.makeText(MianBanJiActivity3.this,"初始化失败"+e.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR).show();

            }
        }

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//             //   SystemClock.sleep(4000);
//                //w = cameraView.getMeasuredWidth();
//              //  h = cameraView.getMeasuredHeight();
//              //  cameraH = manager.getCameraheight();
//              //  cameraW = manager.getCameraWidth();
//
//             //   Message message = new Message();
//             //   message.what = 999;
//              //  message.obj="3304698169";
//              //  mHandler.sendMessage(message);
////                try {
////                    FacePassCompareResult result= paAccessControl.compare(BitmapFactory.decodeResource(getResources(),R.drawable.aa),BitmapFactory.decodeResource(getResources(),R.drawable.bb),false);
////                    Log.d("MianBanJiActivity3", "result.score:" + result.score);
////                    Log.d("MianBanJiActivity3", "result.compareThreshold:" + result.compareThreshold);
////                } catch (FacePassException e) {
////                    e.printStackTrace();
////                }
//
//                //   s1 = (float) w / (float) cameraH;
//              //  s2 = (float) h / (float) cameraW;
//            }
//        }).start();



        tanChuangThread = new TanChuangThread();
        tanChuangThread.start();

        mRecognizeThread = new RecognizeThread();
        mRecognizeThread.start();
        mFeedFrameThread = new FeedFrameThread();
        mFeedFrameThread.start();


        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NotNull Message msg) {
                switch (msg.what) {
//                    case 111: {
//                        Subject subject = (Subject) msg.obj;
//                        //Log.d("MianBanJiActivity3", "subject:" + subject);
//                        if (subject.getTeZhengMa() != null) {
//                            //  Log.d("MianBanJiActivity3", "ddd3");
//                         //   faceView.setTC(BitmapFactory.decodeFile(MyApplication.SDPATH3 + File.separator + subject.getTeZhengMa() + ".png")
//                            //        , subject.getName(), subject.getDepartmentName());
//
//                            DaKaBean daKaBean=new DaKaBean();
//                            daKaBean.setId2(subject.getTeZhengMa());
//                            daKaBean.setName(subject.getPerson_name());
//                            daKaBean.setBumen(subject.getPerson_type());
//                            daKaBean.setTime2(System.currentTimeMillis());
//                            daKaBeanBox.put(daKaBean);
//
//                        } else {
//                            //  Log.d("MianBanJiActivity3", "ddd4");
//                            soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
//                            //faceView.setTC(BitmapUtil.rotateBitmap(msrBitmap, SettingVar.msrBitmapRotation), subject.getName(), subject.getDepartmentName());
//                        }
//
//                        break;
//                    }
                    case 222: {//关门
                        DengUT.getInstance(baoCunBean).closeDool();
                        break;
                    }
                    case 333:

                        DengUT.getInstance(baoCunBean).openLOED();

                        break;
                    case 444:

                        DengUT.getInstance(baoCunBean).closeLOED();

                        break;
                    case 999:
                        DengUT.getInstance(baoCunBean).openLOED();
                       // showUIResult(2,"","");
                        String card= (String) msg.obj;
                        if (card!=null){
                            try {
                                isLink=true;
                                link_ic_info(card);
                            }catch (Exception e){
                                e.printStackTrace();
                                isLink=false;
                            }
//                           List<Subject> subjectList= subjectBox.query().equal(Subject_.ic_card,card).build().find();
//                           if (subjectList.size()>1){
//                               StringBuilder builder=new StringBuilder();
//                               for (Subject subject:subjectList){
//                                   builder.append(subject.getPerson_name());
//                                   builder.append(",");
//                               }
//                               Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, builder.toString()+"ID卡号重复,打卡失败,请重新设置", TastyToast.LENGTH_LONG, TastyToast.ERROR);
//                               tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                               tastyToast.show();
//                               if (!MianBanJiActivity3.this.isFinishing())
//                                   closePing();
//
//                           }else if (subjectList.size()==1){
//                               DengUT.getInstance(baoCunBean).openDool();
//                               //启动定时器或重置定时器
//                               if (task != null) {
//                                   task.cancel();
//                                   //timer.cancel();
//                                   task = new TimerTask() {
//                                       @Override
//                                       public void run() {
//                                           Message message = new Message();
//                                           message.what = 222;
//                                           mHandler.sendMessage(message);
//                                           //关屏
//                                           Message message2 = new Message();
//                                           message2.what = 444;
//                                           mHandler.sendMessage(message2);
//                                       }
//                                   };
//                                   timer.schedule(task, jidianqi);
//                               } else {
//                                   task = new TimerTask() {
//                                       @Override
//                                       public void run() {
//                                           Message message = new Message();
//                                           message.what = 222;
//                                           mHandler.sendMessage(message);
//                                            //关屏
//                                           Message message2 = new Message();
//                                           message2.what = 444;
//                                           mHandler.sendMessage(message2);
//
//                                       }
//                                   };
//                                   timer.schedule(task, jidianqi);
//                               }
//
//                               faceLinearLayout.setVisibility(View.VISIBLE);
//                               faceName.setText(subjectList.get(0).getPerson_name());
//                               if (paAccessControl!=null){
//                                   try {
//                                       Glide.with(MyApplication.myApplication)
//                                               .load(new BitmapDrawable(getResources(),paAccessControl.getFaceImage(subjectList.get(0).getTeZhengMa().getBytes())))
//                                               .apply(myOptions)
//                                               .into(faceImage);
//                                   } catch (FacePassException e) {
//                                       e.printStackTrace();
//                                   }
//                                   try {
//                                     //  link_shangchuanshualian(subjectList.get(0).getSid(),paAccessControl.getFaceImage(subjectList.get(0).getTeZhengMa().getBytes()),subjectList.get(0).getPeopleType()+"");
//                                   } catch (Exception e) {
//                                       e.printStackTrace();
//                                   }
//                               }
//                               new Thread(new Runnable() {
//                                   @Override
//                                   public void run() {
//                                       SystemClock.sleep(2200);
//                                       runOnUiThread(new Runnable() {
//                                           @Override
//                                           public void run() {
//                                               faceLinearLayout.setVisibility(View.GONE);
//                                           }
//                                       });
//                                   }
//                               }).start();
//                           }else {
////                               Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "此ID卡不存在", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
////                               tastyToast.setGravity(Gravity.CENTER, 0, 0);
////                               tastyToast.show();
//                               soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
//                               if (!MianBanJiActivity3.this.isFinishing())
//                               closePing();
//                           }
                        }

                        break;

                }
                return false;
            }
        });


       // init_NFC();



//        if (baoCunBean.isHuoTi()) {
//            if (SettingVar.cameraId == 1) {
//                manager2.open(getWindowManager(), 0, cameraWidth, cameraHeight, SettingVar.cameraPreviewRotation2);//最后一个参数是红外预览方向
//            } else {
//                manager2.open(getWindowManager(), 1, cameraWidth, cameraHeight, SettingVar.cameraPreviewRotation2);//最后一个参数是红外预览方向
//            }
//        }

//        if (jiqiType==0 || jiqiType==1){
//          //  dogThread=new DogThread();
//          //  dogThread.start();
//
//            try {
//                mFuncs = new Function(this, mHandler);
//                loc_readerHandle = mFuncs.lc_init_ex(1, "/dev/ttyS1".toCharArray(), Integer.parseInt("9600"));
//                if(loc_readerHandle == -1)
//                {
//                    Toast.makeText(getApplicationContext(), "连接读卡器失败", Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    int aa= mFuncs.lc_beep(loc_readerHandle, 2);
//                    Log.d("MianBanJiActivity3", loc_readerHandle+"   "+ aa);
//                    mReadThread2 = new ReadThread2();
//                    mReadThread2.start();
//                }
//            }catch (NoClassDefFoundError error){
//                Log.d("MianBanJiActivity3", error.getMessage()+"");
//            }
//        }
//        if (jiqiType==2){
//            try {
//                SerialPort mSerialPort = MyApplication.myApplication.getSerialPort();
//                //mOutputStream = mSerialPort.getOutputStream();
//                mInputStream = mSerialPort.getInputStream();
//                mReadThread = new ReadThread();
//                mReadThread.start();
//            } catch (Exception e) {
//                Log.d("MianBanJiActivity", e.getMessage() + "dddddddd");
//            }
//        }

        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netWorkStateReceiver, filter);
        }

        guanPing();//关屏
        DengUT.getInstance(baoCunBean).openLOED();

        //登录后台
        link_loging(baoCunBean.getTuisongDiZhi());

        if (jiqiType==0||jiqiType==1){
            if (lztek!=null){
                mReadThread3 = new ReadThread3();
                mReadThread3.start();
            }
        }

    }

    public void stopMedie() {
        soundPool.stop(1);
        soundPool.stop(2);
        soundPool.stop(3);
        soundPool.stop(4);
        soundPool.stop(5);
    }

    private String  byteToHexString(byte mByte)
    {
        String hexStr;

        hexStr = Integer.toHexString(mByte & 0xff);
        if(hexStr.length() == 1)
            hexStr = '0'+ hexStr;

        return hexStr;
    }

    @OnClick(R.id.root_layout)
    public void onViewClicked() {
        if (baoCunBean.isShowShiPingLiu()){
            MiMaDialog3 miMaDialog=new MiMaDialog3(MianBanJiActivity3.this,baoCunBean.getMima2());
            WindowManager.LayoutParams params= miMaDialog.getWindow().getAttributes();
            params.width=dw;
            params.height=dh+60;
            miMaDialog.getWindow().setGravity(Gravity.CENTER);
            miMaDialog.getWindow().setAttributes(params);
            miMaDialog.show();
        }
    }



//    private class ReadThread extends Thread {
//        boolean isIterrupt;
//        @Override
//        public void run() {
//            super.run();
//            while (!isIterrupt) {
//                int size;
//                try {
//                    final byte[] buffer = new byte[64];
//                    if (mInputStream == null) return;
//                    size = mInputStream.read(buffer);
//                    if (size > 0) {
//                       //  Log.d("ReadThread", "buffer.length:" + byteToString(buffer));
//                       //  Log.d("ReadThread", new String(buffer));
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                readdd(buffer);
//                            }
//                        });
//                    }
//                    SystemClock.sleep(400);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return;
//                }
//            }
//        }
//
//        @Override
//        public void interrupt() {
//            isIterrupt = true;
//            super.interrupt();
//        }
//    }

//    private class ReadThread2 extends Thread {
//        boolean isIterrupt;
//        @Override
//        public void run() {
//            super.run();
//            while (!isIterrupt) {
//                int st = 0;
//                byte[] rData = new byte[128];
//                int[] rlen = new int[2];
//                if (loc_readerHandle!=-1){
//                    //  Log.d("MianBanJiActivity3", "ffffffff:"+loc_readerHandle);
//                    st = mFuncs.lc_getAutoReturnedData(loc_readerHandle, rData, rlen);
//                    //  Log.d("MianBanJiActivity3", "hh哈哈:"+st);
//                    if (st == 0)
//                    {
//                    //    Log.d("ReadThread2", "rData:" + );
//                        String sdfds = new String(rData);
//                        sdfds = sdfds.substring(0, 10);
//                        Log.d("ReadThread2", sdfds);
////                        StringBuilder showStr= new StringBuilder();
////                        int len=rlen[0];
////                        for(int i= 0; i<len; i++)
////                            showStr.append(byteToHexString(rData[i]));
////                        Log.d("ReadThread2", showStr.toString());
//                        Message message = new Message();
//                        message.what = 999;
//                        message.obj=sdfds.trim();
//                        mHandler.sendMessage(message);
//                    }
//                }
//                SystemClock.sleep(400);
//            }
//        }
//
//        @Override
//        public void interrupt() {
//            isIterrupt = true;
//            super.interrupt();
//        }
//    }


    private static boolean isR=false;
    private class ReadThread3 extends Thread {
        boolean isIterrupt;
        @Override
        public void run() {
            super.run();
            while (!isIterrupt) {
                    SystemClock.sleep(200);
                    final int value = lztek.getGpioValue(218);
                    // Log.d("MianBanJiActivity3", "value:" + value);
                    if (value==1){//有人
                        isCLOSDLED=true;
                         Log.d("MianBanJiActivity3", "value:" + value);
                        isR=true;
                        Message message = new Message();
                        message.what = 333;
                        mHandler.sendMessage(message);

                        if (task != null)
                            task.cancel();
                        SystemClock.sleep(2000);

                    }else {//没人
                        if (isR){
                            isR=false;
                            //  Log.d("MianBanJiActivity3", "valuettttt:" + value);
                            //启动定时器或重置定时器
                            if (task != null) {
                                task.cancel();
                                //timer.cancel();
                                task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (isCLOSDLED){
                                            Message message = new Message();
                                            message.what = 444;
                                            mHandler.sendMessage(message);
                                        }
                                    }
                                };
                                try {
                                    timer.schedule(task, 10000);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            } else {
                                task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (isCLOSDLED){
                                            Message message = new Message();
                                            message.what = 444;
                                            mHandler.sendMessage(message);
                                        }
                                    }
                                };
                                try {
                                    timer.schedule(task, 10000);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
            }
        }
        @Override
        public void interrupt() {
            isIterrupt = true;
            super.interrupt();
        }
    }



    //涂鸦转10位10进制数字
    private void readdd(byte[] idid) {

        StringBuilder builder = new StringBuilder();
        String sdfds = byteToString(idid);
        long d=0;
        if (sdfds != null) {
            sdfds = sdfds.substring(6, 14);
            Log.d("ReadThread", sdfds);
            if(sdfds.length() == 8) {
                for(int i = 0; i<4; i++) {
                    String str = sdfds.substring(sdfds.length()-2 * (i+1), sdfds.length()-2*i);
                    builder.append(str);
                }
            }
             d = Long.valueOf(builder.toString(),16);   //d=255
            Log.d("ReadThread", "builder:" + d);
        } else {
            return;
        }
        //306721218
        String str= addO(d+"");
        Message message = new Message();
        message.what = 999;
        message.obj=str;
        mHandler.sendMessage(message);

    }

    private String addO(String ss){
        if (ss.length()<10){
            ss="0"+ss;
            addO(ss);
        }
        return ss;
    }

    @Override
    protected void onResume() {
        Log.d("MianBanJiActivity3", "重新开始");
        super.onResume();

        manager.open(getWindowManager(), SettingVar.cameraId, cameraWidth, cameraHeight);//前置是1


//        if (jiqiType==2) {
//            Sensor defaultSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//            sm.registerListener(this, defaultSensor, SensorManager.SENSOR_DELAY_NORMAL);
//            if (mTimerTask == null) {
//                mTimerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//                        if (juli > 0 && juli<8200) {
//                            isPM2 = true;
//                            //有人
//                            if (isPM) {
//                                isPM = false;
//
//                                pm = 0;
//                                Message message = new Message();
//                                message.what = 333;
//                                mHandler.sendMessage(message);
//                            }
//                        } else {
//                            isPM = true;
//                            if (isPM2) {
//                                pm++;
//                                if (pm == 8) {
//                                    Message message = new Message();
//                                    message.what = 444;
//                                    mHandler.sendMessage(message);
//                                    isPM2 = false;
//
//                                    pm = 0;
//
//                                    if (DengUT.isOPENRed) {
//                                        DengUT.isOPENRed = false;
//                                        DengUT.getInstance(baoCunBean).closeRed();
//                                    }
//                                    if (DengUT.isOPENGreen) {
//                                        DengUT.isOPENGreen = false;
//                                        DengUT.getInstance(baoCunBean).closeGreen();
//                                    }
//                                    if (DengUT.isOPEN) {
//                                        DengUT.isOPEN = false;
//                                        DengUT.getInstance(baoCunBean).closeWrite();
//                                    }
//                                }
//                            }
//                        }
//                    }
//                };
//            }
//            if (mTimer == null) {
//                mTimer = new Timer();
//            }
//            mTimer.schedule(mTimerTask, 0, 1000);
//        }



    }


//    @Override
//    public void onSensorChanged(SensorEvent event) {
//     //   Log.e("距离", "" + event.values[0]);
//        juli = event.values[0];
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i) {
//
//    }


    @Override
    public void onNewIntent(Intent intent) {
        //  super.onNewIntent(intent);
        // Log.d("SheZhiActivity2", "intent:" + intent);
        //processIntent(intent);
    }



//
//    @Override
//    public void onStarted(String ip) {
//        Log.d("MianBanJiActivity3", "小服务器启动" + ip);
//    }
//
//
//    @Override
//    public void onStopped() {
//        Log.d("MianBanJiActivity3", "小服务器停止");
//    }
//
//    @Override
//    public void onException(Exception e) {
//        Log.d("MianBanJiActivity3", "小服务器异常" + e);
//    }



    /* 相机回调函数 */
    @Override
    public void onPictureTaken(CameraPreviewData cameraPreviewData) {
        /* 如果SDK实例还未创建，则跳过 */
        if (paAccessControl == null) {
            return;
        }
        /* 将相机预览帧转成SDK算法所需帧的格式 FacePassImage */
        FacePassImage image;
        try {
            image = new FacePassImage(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height, SettingVar.faceRotation, FacePassImageType.NV21);
        } catch (FacePassException e) {
            e.printStackTrace();
            return;
        }
        mFeedFrameQueue.offer(image);
    }

    private class FeedFrameThread extends Thread {
        boolean isIterrupt;

        @Override
        public void run() {
            while (!isIterrupt) {
                try {
                    FacePassImage image = mFeedFrameQueue.take();
                    /* 将每一帧FacePassImage 送入SDK算法， 并得到返回结果 */
                    FacePassDetectionResult detectionResult = null;
                    detectionResult = paAccessControl.feedFrame(image);
                   // Log.d("FeedFrameThread", "1 mDetectResultQueue.size = " + mDetectResultQueue.size());
                   // Log.d("FeedFrameThread", "detectionResult:" + detectionResult);
                    if (detectionResult == null || detectionResult.faceList.length <= 0) {
                       // Log.d("FeedFrameThread", "没人"+DengUT.isOPEN);
                        if (DengUT.isOPEN || DengUT.isOPENRed || DengUT.isOPENGreen) {
                            Log.d("MianBanJiActivity3", "没人2");
                            DengUT.isOPEN = false;
                            DengUT.isOPENGreen = false;
                            DengUT.isOPENRed = false;
                            DengUT.isOpenDOR = false;
                            DengUT.getInstance(baoCunBean).closeWrite();
                            showUIResult(1,"","");
                            //启动定时器或重置定时器
                            if (task2 != null) {
                                task2.cancel();
                                //timer.cancel();
                                task2 = new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (!isCLOSDLED){
                                            Message message = new Message();
                                            message.what = 444;
                                            mHandler.sendMessage(message);
                                        }
                                    }
                                };
                                if (timer2!=null)
                                timer2.schedule(task2, 5000);
                            } else {
                                task2 = new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (!isCLOSDLED){
                                            Message message = new Message();
                                            message.what = 444;
                                            mHandler.sendMessage(message);
                                        }
                                    }
                                };
                                if (timer2!=null)
                                timer2.schedule(task2, 5000);
                            }
                        }
                    }else {
                        final FacePassFace[] bufferFaceList = detectionResult.faceList;
                        FacePassMouthOccAttr attr=bufferFaceList[0].mouthOccAttr;
                        if (attr.is_valid){//有效
                            String kouzhao="";
                            switch (attr.mouth_occ_status){
                                case 0:
                                    kouzhao="未佩戴口罩";
                                    break;
                                case 1:
                                    kouzhao="面具遮挡";
                                    break;
                                case 2:
                                    kouzhao="已佩戴口罩";
                                    break;
                                case 3:
                                    kouzhao="其他遮挡";
                                    break;
                                default:
                                    kouzhao="";
                                    break;
                            }
                            String finalKouzhao = kouzhao;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvFaceTips_Ir.setText(finalKouzhao);
                                }
                            });
                        }

                        /*离线模式，将识别到人脸的，message不为空的result添加到处理队列中*/
                        if (detectionResult.message.length != 0) {
                            //  Log.d("FeedFrameThread", "插入");
                            if (!DengUT.isOPEN) {
                                DengUT.isOPEN = true;
                                DengUT.getInstance(baoCunBean).openWrite();
                                showUIResult(2,"","");
                            }
                            mDetectResultQueue.offer(detectionResult);
                            //   Log.d("ggggg", "1 mDetectResultQueue.size = " + mDetectResultQueue.size());
                        }

                        if (!isCLOSDLED){
                            if (task2 != null)
                                task2.cancel();
                            Message message = new Message();
                            message.what = 333;
                            mHandler.sendMessage(message);
                           // Log.d("FeedFrameThread", group_name);
                        }
                    }
                    //     }

                } catch (InterruptedException | FacePassException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void interrupt() {
            isIterrupt = true;
            super.interrupt();
        }
    }


    private class TanChuangThread extends Thread {
        boolean isRing;

        @Override
        public void run() {
            while (!isRing) {
                try {
                    ZhiLingBean commandsBean = linkedBlockingQueue.take();
                    List<String> successfulList=new ArrayList<>();
                    List<FailedPersonBean> failedList=new ArrayList<>();
                    if (commandsBean.getCode()==-99999){//删除
                        JSONArray array= commandsBean.getJsonObject().getJSONObject("params").getJSONArray("Ids");//拿到id列表
                        int size=array.length();
                        for (int i=0;i<size;i++){
                            String idid=array.get(i).toString();
                            try {
                                Log.d("TanChuangThread",idid);
                                List<Subject> subjects=subjectBox.query().equal(Subject_.person_id,idid).build().find();
                                for (Subject subject:subjects){
                                    paAccessControl.deleteFace(subject.getTeZhengMa().getBytes());
                                    subjectBox.remove(subject);
                                }
                                successfulList.add(idid);
                            }catch (Exception e){
                                failedList.add(new FailedPersonBean(idid,e.getMessage()+""));
                            }
                        }
                        //提交记录
                        link_sync_person(successfulList,failedList,commandsBean.getJsonObject());

                    }else {//新增修改
                        for (ZhiLingBean.PersonListBean personListBean : commandsBean.getPerson_list()) {
                            FacePassAddFaceResult detectResult = null;
                            Bitmap bitmap = null;
                            try {
                                if (personListBean.getFace_list()!=null && personListBean.getFace_list().get(0) != null && !personListBean.getFace_list().get(0).getImg_url().equals("")){
                                    bitmap = Glide.with(MianBanJiActivity3.this).asBitmap()
                                            .load(personListBean.getFace_list().get(0).getImg_url())
                                            // .sizeMultiplier(0.5f)
                                            .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                            .get();
                                }else {
                                    failedList.add(new FailedPersonBean(personListBean.getPerson_id(),"图片地址为空"));
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                                failedList.add(new FailedPersonBean(personListBean.getPerson_id(),"图片下载失败"+e.getMessage()));
                            }
                            if (bitmap != null) {//有图片
                                BitmapUtil.saveBitmapToSD(bitmap, MyApplication.SDPATH3,  "aaabbb.jpg");
                               // File filef = new File(MyApplication.SDPATH3 + File.separator +  "aaabbb.png");
                               // Log.d("TanChuangThread",  "未压缩前:filef.length():" + filef.length());
                                File file = Luban.with(MianBanJiActivity3.this)
                                        .load(MyApplication.SDPATH3 + File.separator +  "aaabbb.jpg")
                                        .ignoreBy(600)
                                        .setTargetDir(MyApplication.SDPATH3 + File.separator)
                                        .get(MyApplication.SDPATH3 + File.separator +  "aaabbb.jpg");

                                if (file == null) {
                                    Log.d("TanChuangThread", "图片压缩失败");
                                    failedList.add(new FailedPersonBean(personListBean.getPerson_id(),"图片压缩失败"));
                                    continue;
                                }
                               // Log.d("TanChuangThread",  "压缩后:file.length():" + file.length());
                                detectResult = paAccessControl.addFace(BitmapFactory.decodeFile(file.getAbsolutePath()));
                                if (detectResult != null && detectResult.result==0) {
                                    byte [] faceToken=detectResult.faceToken;
                                    Subject sb=null;
                                    try {
                                        sb=subjectBox.query().equal(Subject_.person_id,personListBean.getPerson_id()).build().findUnique();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        failedList.add(new FailedPersonBean(personListBean.getPerson_id(),e.getMessage()));
                                    }
                                    if (sb!=null){
                                        paAccessControl.deleteFace(sb.getTeZhengMa().getBytes());
                                        sb.setBirthday(personListBean.getBirthday());
                                        sb.setPerson_name(personListBean.getPerson_name());
                                        sb.setCount(personListBean.getValid_time().getCount());
                                        sb.setEnd_time(personListBean.getValid_time().getEnd_time());
                                        sb.setFingerTemplate(personListBean.getFingerTemplate());
                                        sb.setGroup_id(personListBean.getGroup_id());
                                        sb.setGroup_name(personListBean.getGroup_name());
                                        sb.setGroup_type(personListBean.getGroup_type());
                                        sb.setIc_card(personListBean.getIc_card());
                                        sb.setId_card(personListBean.getId_card());
                                        sb.setPerson_id(personListBean.getPerson_id());
                                        sb.setPerson_type(personListBean.getPerson_type());
                                        sb.setSex(personListBean.getSex());
                                        sb.setStart_time(personListBean.getValid_time().getStart_time());
                                        sb.setTeZhengMa(new String(faceToken));
                                        paAccessControl.bindGroup(group_name,faceToken);
                                        subjectBox.put(sb);
                                        successfulList.add(personListBean.getPerson_id());
                                    }else {
                                        Subject subject = new Subject();
                                        subject.setBirthday(personListBean.getBirthday());
                                        subject.setPerson_name(personListBean.getPerson_name());
                                        subject.setCount(personListBean.getValid_time().getCount());
                                        subject.setEnd_time(personListBean.getValid_time().getEnd_time());
                                        subject.setFingerTemplate(personListBean.getFingerTemplate());
                                        subject.setGroup_id(personListBean.getGroup_id());
                                        subject.setGroup_name(personListBean.getGroup_name());
                                        subject.setGroup_type(personListBean.getGroup_type());
                                        subject.setIc_card(personListBean.getIc_card());
                                        subject.setId_card(personListBean.getId_card());
                                        subject.setPerson_id(personListBean.getPerson_id());
                                        subject.setPerson_type(personListBean.getPerson_type());
                                        subject.setSex(personListBean.getSex());
                                        subject.setStart_time(personListBean.getValid_time().getStart_time());
                                        subject.setTeZhengMa(new String(faceToken));
                                        paAccessControl.bindGroup(group_name,faceToken);
                                        subjectBox.put(subject);
                                        successfulList.add(personListBean.getPerson_id());
                                    }
                                } else {
                                    if (detectResult!=null)
                                        failedList.add(new FailedPersonBean(personListBean.getPerson_id(),"图片质量不合格,错误码:"+detectResult.result));
                                    else
                                        failedList.add(new FailedPersonBean(personListBean.getPerson_id(),"检测图片失败"));
                                }
                            } else {//没图片加入失败记录
                                failedList.add(new FailedPersonBean(personListBean.getPerson_id(),"下载图片失败"));
                            }
                        }
                        //提交记录
                        link_sync_person(successfulList,failedList,commandsBean.getJsonObject());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        @Override
        public void interrupt() {
            isRing = true;
            // Log.d("RecognizeThread", "中断了弹窗线程");
            super.interrupt();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MianBanJiActivity3", "暂停");


        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
      //  if (sm != null)
         //   sm.unregisterListener(this);

    }


    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        SharedPreferences preferences = getSharedPreferences(SettingVar.SharedPrefrence, Context.MODE_PRIVATE);
        SettingVar.isSettingAvailable = preferences.getBoolean("isSettingAvailable", SettingVar.isSettingAvailable);
        SettingVar.cameraId = preferences.getInt("cameraId", SettingVar.cameraId);
        SettingVar.faceRotation = preferences.getInt("faceRotation", SettingVar.faceRotation);
        SettingVar.cameraPreviewRotation = preferences.getInt("cameraPreviewRotation", SettingVar.cameraPreviewRotation);
        SettingVar.cameraFacingFront = preferences.getBoolean("cameraFacingFront", SettingVar.cameraFacingFront);
        SettingVar.cameraPreviewRotation2 = preferences.getInt("cameraPreviewRotation2", SettingVar.cameraPreviewRotation2);
        SettingVar.faceRotation2 = preferences.getInt("faceRotation2", SettingVar.faceRotation2);
        SettingVar.msrBitmapRotation = preferences.getInt("msrBitmapRotation", SettingVar.msrBitmapRotation);

        setContentView(R.layout.activity_mianbanji3);

        ButterKnife.bind(this);

        ImageView shezhi = findViewById(R.id.shezhi);
        shezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MiMaDialog4 miMaDialog = new MiMaDialog4(MianBanJiActivity3.this, baoCunBean.getMima());
                WindowManager.LayoutParams params = miMaDialog.getWindow().getAttributes();
                params.width = dw;
                params.height = dh;
//                miMaDialog.getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                miMaDialog.getWindow().setAttributes(params);
                miMaDialog.show();
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        heightPixels = displayMetrics.heightPixels;
        widthPixels = displayMetrics.widthPixels;
        SettingVar.mHeight = heightPixels;
        SettingVar.mWidth = widthPixels;
        /* 初始化界面 */
        manager = new CameraManager();
        cameraView = (CameraPreview) findViewById(R.id.preview);
        manager.setPreviewDisplay(cameraView);
        /* 注册相机回调函数 */
        manager.setListener(this);

      //  manager2 = new CameraManager2();
      //  cameraView2 = findViewById(R.id.preview2);
      //  manager2.setPreviewDisplay(cameraView2);
        /* 注册相机回调函数 */
      //  manager2.setListener(this);

        tvName_Ir = findViewById(R.id.tvName_Ir);//名字
        tvTime_Ir = findViewById(R.id.tvTime_Ir);//时间
        tvFaceTips_Ir = findViewById(R.id.tvFaceTips_Ir);//识别信息提示
        layout_loadbg_Ir = findViewById(R.id.layout_loadbg_Ir);//头像区域的显示的底图背景

        layout_true_gif_Ir = findViewById(R.id.layout_true_gif_Ir);
        layout_error_gif_Ir = findViewById(R.id.layout_error_gif_Ir);
        iv_true_gif_in_Ir = findViewById(R.id.iv_true_gif_in_Ir);
        iv_true_gif_out_Ir = findViewById(R.id.iv_true_gif_out_Ir);
        iv_error_gif_in_Ir = findViewById(R.id.iv_error_gif_in_Ir);
        iv_error_gif_out_Ir = findViewById(R.id.iv_error_gif_out_Ir);
        tvTitle_Ir = findViewById(R.id.tvTitle_Ir);

        //region 动画
        gifClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_anim_face_clockwise);
        gifAntiClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_anim_face_anti_clockwise);
        lir_gif = new LinearInterpolator();//设置为匀速旋转
        gifClockwise.setInterpolator(lir_gif);
        gifAntiClockwise.setInterpolator(lir_gif);

        iv_true_gif_out_Ir.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        iv_error_gif_out_Ir.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        iv_true_gif_out_Ir.startAnimation(gifClockwise);
        iv_error_gif_out_Ir.startAnimation(gifClockwise);
        AssetManager mgr = getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/hua.ttf");
        tvTitle_Ir.setTypeface(tf);
        if (baoCunBean.getWenzi1() == null) {
            tvTitle_Ir.setText("请设置公司名称");
        } else {
            tvTitle_Ir.setText(baoCunBean.getWenzi1());
        }
        if (baoCunBean.getLogo()!=null){
            try {
                logo.setImageBitmap(BitmapUtil.base64ToBitmap(baoCunBean.getLogo()));
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        showUIResult(1,"","");

    }



    private class RecognizeThread extends Thread {

        boolean isInterrupt;

        @Override
        public void run() {
            while (!isInterrupt) {
                try {
                    FacePassDetectionResult detectionResult = mDetectResultQueue.take();
                    // byte[] detectionResult = mDetectResultQueue.take();
                    FacePassRecognitionResult[] recognizeResult = paAccessControl.recognize(group_name, detectionResult.message);
                  //   Log.d("RecognizeThread", "识别线程");
                    if (recognizeResult != null && recognizeResult.length > 0) {
                        // Log.d("RecognizeThread", "recognizeResult.length:" + recognizeResult.length);
                        for (FacePassRecognitionResult result : recognizeResult) {
                          //  Log.d("RecognizeThread", "result.trackId:" + result.trackId);
                            //String faceToken = new String(result.faceToken);
                         //   Log.d("RecognizeThread", "paAccessControl.getConfig().searchThreshold:" + paAccessControl.getConfig().searchThreshold);
                            if (FacePassRecognitionResultType.RECOG_OK == result.facePassRecognitionResultType) {
                                //识别的
                                if (!isLink){
                                    Subject subject = subjectBox.query().equal(Subject_.teZhengMa, new String(result.faceToken)).build().findUnique();
                                    // Log.d("RecognizeThread", "subject:" + subject);
                                    if (subject != null) {
                                        try {
                                            long t1=Long.parseLong(DateUtils.dataOne(subject.getEnd_time()));
                                            if (t1<System.currentTimeMillis()){//已经过期
                                                paAccessControl.deleteFace(subject.getTeZhengMa().getBytes());
                                                subjectBox.remove(subject);
                                                DengUT.isOPEN=true;
                                                paAccessControl.reset();
                                                return;
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        for (int i = 0; i < detectionResult.faceList.length; i++) {
                                            FacePassImage images = detectionResult.images[i];
                                            if (images.trackId == result.trackId) {
                                                final Bitmap fileBitmap = nv21ToBitmap.nv21ToBitmap(images.image, images.width, images.height);
                                                link_shangchuanshualian(subject.getPerson_id(), fileBitmap,result.detail.searchScore+"","0",subject.getPerson_type(),subject.getPerson_name());
                                                //  String paths = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ruitongzipmbj";
                                                break;
                                            }
                                        }
                                       // showUIResult(4,subject.getPerson_name(),"认证通过");
                                        //DengUT.isOPEN = true;
                                }else {//刷卡模式
                                        for (int i = 0; i < detectionResult.faceList.length; i++) {
                                            FacePassImage images = detectionResult.images[i];
                                            if (images.trackId == result.trackId) {
                                                cardBitmap = nv21ToBitmap.nv21ToBitmap(images.image, images.width, images.height);
                                                break;
                                            }
                                        }
                                 }
                                 //   msrBitmap = nv21ToBitmap.nv21ToBitmap(result.feedback.rgbImage.image, result.feedback.rgbImage.width, result.feedback.rgbImage.height);
                                } else {
                                    EventBus.getDefault().post("没有查询到人员信息");
                                }

                            } else {
                               // Log.d("RecognizeThread", "未识别");
                                //未识别的
                                // 防止concurrentHashMap 数据过多 ,超过一定数据 删除没用的
                                if (!isLink){
                                    if (concurrentHashMap.size() > 20) {
                                        concurrentHashMap.clear();
                                    }
                                    if (concurrentHashMap.get(result.trackId) == null) {
                                        //找不到新增
                                        concurrentHashMap.put(result.trackId, 1);
                                    } else {
                                        //找到了 把value 加1
                                        concurrentHashMap.put(result.trackId, (concurrentHashMap.get(result.trackId)) + 1);
                                    }
                                    //判断次数超过3次
                                    if (concurrentHashMap.get(result.trackId) == cishu) {

                                        if (!DengUT.isOPENRed) {
                                            DengUT.isOPENRed = true;
                                            DengUT.getInstance(baoCunBean).openRed();
                                        }
                                        //  long time=System.currentTimeMillis();
                                        for (int i = 0; i < detectionResult.faceList.length; i++) {
                                            FacePassImage images = detectionResult.images[i];
                                            if (images.trackId == result.trackId) {
                                                Bitmap fileBitmap = nv21ToBitmap.nv21ToBitmap(images.image, images.width, images.height);
                                                link_shangchuanshualian("", fileBitmap,result.detail.searchScore+"","1","5","");
                                                //    Log.d("RecognizeThread", "detectionResult.faceList[i].mouthOccAttr.is_valid:" + detectionResult.faceList[i].mouthOccAttr.is_valid);
                                                //   Log.d("RecognizeThread", "detectionResult.faceList[i].mouthOccAttr.mouth_occ_status:" + detectionResult.faceList[i].mouthOccAttr.mouth_occ_status);
                                                break;
                                            }
                                        }
                                      //  showUIResult(3,"陌生人","");
                                      //  DengUT.isOPEN = true;

                                        //   msrBitmap = nv21ToBitmap.nv21ToBitmap(result.feedback.rgbImage.image, result.feedback.rgbImage.width, result.feedback.rgbImage.height);
                                        //   Log.d("RecognizeThread", "入库"+tID);
                                    }
                                }else {
                                    for (int i = 0; i < detectionResult.faceList.length; i++) {
                                        FacePassImage images = detectionResult.images[i];
                                        if (images.trackId == result.trackId) {
                                            cardBitmap = nv21ToBitmap.nv21ToBitmap(images.image, images.width, images.height);
                                            break;
                                        }
                                    }
                                }
                            }

                        }
                    }

                } catch (InterruptedException | FacePassException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void interrupt() {
            isInterrupt = true;
            super.interrupt();
        }

    }




    @Override
    protected void onStop() {
        Log.d("MianBanJiActivity3", "停止");


        if (manager != null) {
            manager.release();
        }
//        if (manager2 != null) {
//            manager2.release();
//        }
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d("MianBanJiActivity3", "onRestart");
        super.onRestart();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MianBanJiActivity3", "onStart");
    }


    @Override
    protected void onDestroy() {
        Log.d("MianBanJiActivity3", "onDestroy");

//        if (mReadThread != null) {
//            mReadThread.interrupt();
//        }

//        if (jiqiType==0){
//            try {//停止喂狗
//                HwitManager.HwitStopSoftWatchDog(MianBanJiActivity3.this);
//            }catch (NoClassDefFoundError error){
//                error.printStackTrace();
//            }
//        }
//        if (jiqiType==1 && lztek!=null){
//            try {//停止喂狗
//               lztek.watchDogDisable();
//            }catch (NoClassDefFoundError error){
//                error.printStackTrace();
//            }
//        }

     //   if (serverManager!=null)
      //      serverManager.stopServer();

//        if (dogThread != null) {
//            dogThread.isIterrupt=true;
//            dogThread.interrupt();
//        }

//        if (mReadThread != null) {
//            mReadThread.isIterrupt=true;
//            mReadThread.interrupt();
//        }
//        if (mReadThread2 != null) {
//            mReadThread2.isIterrupt=true;
//            mReadThread2.interrupt();
//        }
        if (mReadThread3 != null) {
            mReadThread3.isIterrupt=true;
            mReadThread3.interrupt();
        }

        if (linkedBlockingQueue != null) {
            linkedBlockingQueue.clear();
        }
        if (mFeedFrameQueue != null) {
            mFeedFrameQueue.clear();
        }
        if (mFeedFrameThread != null) {
            mFeedFrameThread.isIterrupt = true;
            mFeedFrameThread.interrupt();
        }

        if (tanChuangThread != null) {
            tanChuangThread.isRing = true;
            tanChuangThread.interrupt();
        }

        if (mRecognizeThread != null) {
            mRecognizeThread.isInterrupt = true;
            mRecognizeThread.interrupt();
        }

        unregisterReceiver(timeChangeReceiver);
        unregisterReceiver(netWorkStateReceiver);
        EventBus.getDefault().unregister(this);//解除订阅
        if (manager != null) {
            manager.release();
        }

        if (task != null)
            task.cancel();
        timer.cancel();

        if (task2 != null)
            task2.cancel();
        timer2.cancel();


        DengUT.getInstance(baoCunBean).closeWrite();
        DengUT.getInstance(baoCunBean).closeGreen();
        DengUT.getInstance(baoCunBean).closeRed();
        Log.d("MianBanJiActivity3", "onDestroy");
        super.onDestroy();
        MyApplication.myApplication.removeActivity(this);
    }

  //  private static final int REQUEST_CODE_CHOOSE_PICK = 1;


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            if (keyCode == KeyEvent.KEYCODE_MENU) {
//              // startActivity(new Intent(MianBanJiActivity3.this, SheZhiActivity2.class));
//              //  finish();
//            }
//
//        }
//
//        return super.onKeyDown(keyCode, event);
//
//    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(String event) {

        if (event.equals("ditu123")) {
            // if (baoCunBean.getTouxiangzhuji() != null)
            //    daBg.setImageBitmap(BitmapFactory.decodeFile(baoCunBean.getTouxiangzhuji()));
            baoCunBean = baoCunBeanDao.get(123456L);

            //   Log.d("MainActivity101", "dfgdsgfdgfdgfdg");
            return;
        }

        if (event.equals("kaimen")) {
            menjing1();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(jidianqi);
                    menjing2();
                }
            }).start();
            return;
        }
        if (event.equals("guanbimain")) {
            finish();
            Log.d("MianBanJiActivity3", "关闭Mianbanjia");
            return;
        }
        if (event.equals("configs")){
            //更新配置
            baoCunBean = baoCunBeanDao.get(123456L);
            if (baoCunBean.getWenzi1() == null) {
                tvTitle_Ir.setText("请设置公司名称");
            } else {
                tvTitle_Ir.setText(baoCunBean.getWenzi1());
            }
            if (baoCunBean.getJidianqi()!=0){
                jidianqi=baoCunBean.getJidianqi();
            }
            if (baoCunBean.getMoshengrenPanDing()!=0){
                cishu=baoCunBean.getMoshengrenPanDing();
            }
            if (baoCunBean.getLogo()!=null){
                try {
                    logo.setImageBitmap(BitmapUtil.base64ToBitmap(baoCunBean.getLogo()));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        if (event.equals("mFacePassHandler")) {
            paAccessControl = MyApplication.myApplication.getFacePassHandler();
            return;
        }

        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, event, TastyToast.LENGTH_LONG, TastyToast.INFO);
        tastyToast.setGravity(Gravity.CENTER, 0, 0);
        tastyToast.show();

    }


    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case Intent.ACTION_TIME_TICK:
                    //mianBanJiView.setTime(DateUtils.time(System.currentTimeMillis()+""));
                    // String riqi11 = DateUtils.getWeek(System.currentTimeMillis()) + "   " + DateUtils.timesTwo(System.currentTimeMillis() + "");
                    //  riqi.setTypeface(tf);

                    String xiaoshiss = DateUtils.timeMinute(System.currentTimeMillis() + "");
                    if (xiaoshiss.split(":")[0].equals("03") && xiaoshiss.split(":")[1].equals("40")) {
                     Log.d("TimeChangeReceiver", "ssss");

                    }

                    timeUpdataState++;
                    if (timeUpdataState>=6){//5分钟传一次
                        timeUpdataState=1;
                        link_updataState(baoCunBean.getTuisongDiZhi());
                    }

                    break;
                case Intent.ACTION_TIME_CHANGED:
                    //设置了系统时间
                    // Toast.makeText(context, "system time changed", Toast.LENGTH_SHORT).show();
                    break;
                case Intent.ACTION_TIMEZONE_CHANGED:
                    //设置了系统时区的action
                    //  Toast.makeText(context, "system time zone changed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }







//    //数据同步
//    private void link_infoSync() {
//        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        JSONArray array = new JSONArray();
//        LazyList<HuiFuBean> huiFuBeanList = huiFuBeanBox.query().build().findLazy();
//        if (huiFuBeanList.size()==0)
//            return;
//        List<HuiFuBean> huiFuBeans=new ArrayList<>();
//        for (HuiFuBean h:huiFuBeanList){
//            huiFuBeans.add(h);
//        }
//            for (HuiFuBean bean : huiFuBeans) {
//                JSONObject object = new JSONObject();
//                try {
//                    object.put("pepopleId", bean.getPepopleId());
//                    object.put("pepopleType", bean.getPepopleType());
//                    object.put("type", bean.getType());
//                    object.put("msg", bean.getMsg());
//                    object.put("shortId", bean.getShortId());
//                    object.put("serialnumber", JHM);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                array.put(object);
//            }
//
//        Log.d("MianBanJiActivity3", "数据同步：" + array.toString());
//        RequestBody body = RequestBody.create(array.toString(), JSON);
//        Request.Builder requestBuilder = new Request.Builder()
//                .header("Content-Type", "application/json")
//                .post(body)
//                .url(baoCunBean.getHoutaiDiZhi() + "/app/infoSync");
//        // step 3：创建 Call 对象
//        Call call = okHttpClient.newCall(requestBuilder.build());
//        //step 4: 开始异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("AllConnects", "数据同步请求失败" + e.getMessage());
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("AllConnects", "请求成功" + call.request().toString());
//                //获得返回体
//                try {
//                    ResponseBody body = response.body();
//                    String ss = body.string().trim();
//                    Log.d("AllConnects", "数据同步:" + ss);
//                    JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
//
//                    for (HuiFuBean bean : huiFuBeans) {
//                        huiFuBeanBox.remove(bean);
//                    }
//                } catch (Exception e) {
//                    Log.d("WebsocketPushMsg", e.getMessage() + "数据同步");
//                }
//            }
//        });
//    }

    //信鸽信息处理
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(XGBean xgBean) {


    }


    private void guanPing() {
        Intent intent = new Intent();
        intent.setAction("LYD_SHOW_NAVIGATION_BAR");
        intent.putExtra("type", 0);
        this.sendBroadcast(intent);
        sendBroadcast(new Intent("com.android.internal.policy.impl.hideNavigationBar"));
        sendBroadcast(new Intent("com.android.systemui.statusbar.phone.statusclose"));
       //8寸防水面板机
        try {
         Lztek lztek=Lztek.create(MyApplication.myApplication);
         lztek.navigationBarSlideShow(false);
         lztek.hideNavigationBar();
        }catch (NoClassDefFoundError e){
            e.printStackTrace();
        }
        try {
            HwitManager.HwitSetHideSystemBar(MianBanJiActivity3.this);
            HwitManager.HwitSetDisableSlideShowSysBar(1);
        }catch (NoClassDefFoundError error){
            error.printStackTrace();
        }
    }

    private void menjing1() {
        // TPS980PosUtil.setJiaJiPower(1);
        DengUT.getInstance(baoCunBean).openDool();
      //  TPS980PosUtil.setRelayPower(1);
        Log.d("MianBanJiActivity3", "打开");
    }

    private void menjing2() {
        //  TPS980PosUtil.setJiaJiPower(0);
        DengUT.getInstance(baoCunBean).closeDool();
       // TPS980PosUtil.setRelayPower(0);
        Log.d("MianBanJiActivity3", "关闭");
    }



//    private void init_NFC() {
//
//        NfcManager mNfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
//        mNfcAdapter = mNfcManager.getDefaultAdapter();
//        if (mNfcAdapter == null) {
//            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "设备不支持NFC", TastyToast.LENGTH_LONG, TastyToast.INFO);
//            tastyToast.setGravity(Gravity.CENTER, 0, 0);
//            tastyToast.show();
//        } else if (!mNfcAdapter.isEnabled()) {
//            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "请先去设置里面打开NFC开关", TastyToast.LENGTH_LONG, TastyToast.INFO);
//            tastyToast.setGravity(Gravity.CENTER, 0, 0);
//            tastyToast.show();
//        } else if ((mNfcAdapter != null) && (mNfcAdapter.isEnabled())) {
//
//        }
//        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
//
//
//        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
//        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
//
//        if (mNfcAdapter != null) {
//            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
//            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(this.getIntent().getAction())) {
//                processIntent(this.getIntent());
//            }
//        }
//
//
//    }

  //  private void stopNFC_Listener() {
   //     mNfcAdapter.disableForegroundDispatch(this);
   // }

//    public void processIntent(Intent intent) {
//        //  String data = null;
//        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//        // String[] techList = tag.getTechList();
//        // Log.d("Mian", "tag.describeContents():" + tag.describeContents());
//        byte[] ID;
//        //  data = tag.toString();
//        if (tag == null)
//            return;
//        ID = tag.getId();
////        data += "\n\nUID:\n" + byteToString(ID);
////        data += "\nData format:";
////        for (String tech : techList) {
////            data += "\n" + tech;
////        }
////         Log.d("MianBanJiActivity3", byteToString(ID));
//        String sdfds = byteToString(ID);
//        if (sdfds != null) {
//            Log.d("MianBanJiActivity3", sdfds);
//            sdfds = sdfds.toUpperCase();
//            List<IDCardBean> idCardBeanList = idCardBeanBox.query().equal(IDCardBean_.idCard, sdfds).build().find();
//            if (idCardBeanList.size() > 0) {
//                Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "验证成功!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
//                tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                tastyToast.show();
//                soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
//                DengUT.getInstance(baoCunBean).openDool();
//                IDCardBean cardBean=idCardBeanList.get(0);
//               // link_shuaka(sdfds,cardBean.getName());
//                //启动定时器或重置定时器
//                if (task != null) {
//                    task.cancel();
//                    task = new TimerTask() {
//                        @Override
//                        public void run() {
//                            Message message = new Message();
//                            message.what = 222;
//                            mHandler.sendMessage(message);
//                        }
//                    };
//                    timer.schedule(task, 6000);
//                } else {
//                    task = new TimerTask() {
//                        @Override
//                        public void run() {
//                            Message message = new Message();
//                            message.what = 222;
//                            mHandler.sendMessage(message);
//                        }
//                    };
//                    timer.schedule(task, 6000);
//                }
//
//                IDCardTakeBean takeBean=new IDCardTakeBean();
//                takeBean.setIdCard(sdfds);
//                takeBean.setName(cardBean.getName());
//                takeBean.setTime(System.currentTimeMillis());
//                idCardTakeBeanBox.put(takeBean);
//
//            } else {
//                Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "验证失败!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
//                tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                tastyToast.show();
//                soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
//            }
//        }
//
//
//    }

    /**
     * 将byte数组转化为字符串
     *
     * @param src
     * @return
     */
    public static String byteToString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            // System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }


    public class NetWorkStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取所有网络连接的信息
            if (connMgr!=null){
                Network[] networks = connMgr.getAllNetworks();
                //用于存放网络连接信息
                StringBuilder sb = new StringBuilder();
                //通过循环将网络信息逐个取出来
                Log.d("MianBanJiActivity3", "networks.length:" + networks.length);
                if (networks.length == 0) {
                    //没网
                    Log.d("MianBanJiActivity3", "没网2");
//                    if (serverManager != null) {
//                        serverManager.stopServer();
//                        serverManager = null;
//                    }
                }
                for (Network network : networks) {
                    //获取ConnectivityManager对象对应的NetworkInfo对象
                    NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
                    if (networkInfo!=null && networkInfo.isConnected()) {
                        //连接上
                        Log.d("MianBanJiActivity3", "有网2");
//                        if (serverManager != null) {
//                            serverManager.stopServer();
//                            serverManager = null;
//                        }
//                        serverManager = new ServerManager(FileUtil.getIPAddress(getApplicationContext()), baoCunBean.getPort());
//                        serverManager.setMyServeInterface(MianBanJiActivity3.this);
//                        serverManager.startServer();
                        break;
                    }
                }
            }
        }
    }


    /**
     * Activity截获按键事件.发给ScanGunKeyEventHelper
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //  Log.d("Main4Activity", event.getDevice().toString());
        mScanGunKeyEventHelper.analysisKeyEvent(event);
        return true;
        //  return super.dispatchKeyEvent(event);
    }


    //usb读卡器输出
    @Override
    public void onScanSuccess(String barcode) {
       // Log.d("MianBanJiActivity3", barcode+"dddddd");
        if (barcode!=null && !barcode.equals("") && !isLink){
           // Toast.makeText(MianBanJiActivity3.this,barcode+"",Toast.LENGTH_SHORT).show();
            Message message = new Message();
            message.what = 999;
            message.obj=barcode.trim();
            mHandler.sendMessage(message);
        }
    }

    /**
     * 通过Base32将Bitmap转换成Base64字符串
     *
     * @param bit
     * @return
     */
    public String Bitmap2StrByBase64(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 90, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }



    /**
     * 显示结果UI
     *
     * @param state 状态 1 初始状态  2 识别中,出现提示语  3 识别失败  4 识别成功
     */
    protected void showUIResult(final int state, final String name, final String detectFaceTime) {
        if (state==STATE){
            return;
        }else {
            STATE=state;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
              //  Log.d("MianBanJiActivity3", "state:" + state);
                switch (state) {
                    case 1: {//初始状态
                        layout_true_gif_Ir.setVisibility(View.INVISIBLE);//蓝色图片动画
                        layout_error_gif_Ir.setVisibility(View.INVISIBLE);//红色图片动画
                        iv_true_gif_in_Ir.setVisibility(View.INVISIBLE);//蓝色圈内层
                        iv_true_gif_out_Ir.setVisibility(View.INVISIBLE);//蓝色圈外层
                        iv_error_gif_in_Ir.setVisibility(View.INVISIBLE);//红色圈内层
                        iv_error_gif_out_Ir.setVisibility(View.INVISIBLE);//红色圈外层
                        layout_loadbg_Ir.setVisibility(View.INVISIBLE);//识别结果大框
                        tvName_Ir.setVisibility(View.GONE);//姓名
                        tvTime_Ir.setVisibility(View.GONE);//时间
                        tvFaceTips_Ir.setVisibility(View.GONE);//识别提示
                        tvName_Ir.setText("");
                        tvTime_Ir.setText("");
                        tvFaceTips_Ir.setText("");
                        break;
                    }
                    case 2: {//识别中,出现提示语
                        layout_true_gif_Ir.setVisibility(View.VISIBLE);//蓝色图片动画
                        layout_error_gif_Ir.setVisibility(View.INVISIBLE);//红色图片动画
                        iv_true_gif_in_Ir.setVisibility(View.VISIBLE);//蓝色圈内层
                        iv_true_gif_out_Ir.setVisibility(View.VISIBLE);//蓝色圈外层
                        iv_error_gif_in_Ir.setVisibility(View.INVISIBLE);//红色圈内层
                        iv_error_gif_out_Ir.setVisibility(View.INVISIBLE);//红色圈外层
                        layout_loadbg_Ir.setVisibility(View.VISIBLE);//识别结果大框
                        layout_loadbg_Ir.setBackgroundResource(R.mipmap.true_bg);//切换背景
                        tvName_Ir.setVisibility(View.GONE);//姓名
                        tvTime_Ir.setVisibility(View.GONE);//时间
                        tvFaceTips_Ir.setVisibility(View.VISIBLE);//识别提示
                        tvName_Ir.setText("");
                        tvTime_Ir.setText("");
                        tvFaceTips_Ir.setText("识别中,请稍后...");
                        break;
                    }
                    case 3: {//识别失败
                        layout_true_gif_Ir.setVisibility(View.INVISIBLE);//蓝色图片动画
                        layout_error_gif_Ir.setVisibility(View.VISIBLE);//红色图片动画
                        iv_true_gif_in_Ir.setVisibility(View.INVISIBLE);//蓝色圈内层
                        iv_true_gif_out_Ir.setVisibility(View.INVISIBLE);//蓝色圈外层
                        iv_error_gif_in_Ir.setVisibility(View.VISIBLE);//红色圈内层
                        iv_error_gif_out_Ir.setVisibility(View.VISIBLE);//红色圈外层
                        layout_loadbg_Ir.setVisibility(View.VISIBLE);//识别结果大框
                        layout_loadbg_Ir.setBackgroundResource(R.mipmap.error_bg);//切换背景
                        tvName_Ir.setVisibility(View.GONE);//姓名
                        tvTime_Ir.setVisibility(View.VISIBLE);//时间
                        tvFaceTips_Ir.setVisibility(View.VISIBLE);//识别提示
                        tvName_Ir.setText("");
                        tvTime_Ir.setText("无权限通过,请重试");
                     //   tvFaceTips_Ir.setText(kouzhao);
                        break;
                    }
                    case 4: {//识别成功
                        layout_true_gif_Ir.setVisibility(View.VISIBLE);//蓝色图片动画
                        layout_error_gif_Ir.setVisibility(View.INVISIBLE);//红色图片动画
                        iv_true_gif_in_Ir.setVisibility(View.VISIBLE);//蓝色圈内层
                        iv_true_gif_out_Ir.setVisibility(View.VISIBLE);//蓝色圈外层
                        iv_error_gif_in_Ir.setVisibility(View.INVISIBLE);//红色圈内层
                        iv_error_gif_out_Ir.setVisibility(View.INVISIBLE);//红色圈外层
                        layout_loadbg_Ir.setVisibility(View.VISIBLE);//识别结果大框
                        layout_loadbg_Ir.setBackgroundResource(R.mipmap.true_bg);//切换背景
                        tvName_Ir.setVisibility(View.VISIBLE);//姓名
                        tvTime_Ir.setVisibility(View.VISIBLE);//时间
                        tvFaceTips_Ir.setVisibility(View.VISIBLE);//识别提示
                        tvName_Ir.setText(name);
                        tvTime_Ir.setText(detectFaceTime);
                     //   tvFaceTips_Ir.setText(kouzhao);
                        break;
                    }
                }
            }
        });
    }



    private void closeDoor(){
        //启动定时器或重置定时器
        if (task != null) {
            task.cancel();
            //timer.cancel();
            task = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 222;
                    mHandler.sendMessage(message);
                }
            };
            if (timer!=null)
                timer.schedule(task, jidianqi);
        } else {
            task = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 222;
                    mHandler.sendMessage(message);
                }
            };
            if (timer!=null)
                timer.schedule(task, jidianqi);
        }
    }
    private void closePing(){
        //启动定时器或重置定时器
        if (task != null) {
            task.cancel();
            //timer.cancel();
            task = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    //关屏
                    message.what = 444;
                    mHandler.sendMessage(message);
                }
            };
            if (timer!=null)
            timer.schedule(task, jidianqi);
        } else {
            task = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    //关屏
                    message.what = 444;
                    mHandler.sendMessage(message);

                }
            };
            if (timer!=null)
            timer.schedule(task, jidianqi);
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //登录
    private void link_loging(String deviceId){
        if (baoCunBean.getHoutaiDiZhi()==null || baoCunBean.getHoutaiDiZhi().equals("")){
            EventBus.getDefault().post("后台地址不正确");
            return;
        }
        final MediaType JSON=MediaType.parse("application/json; charset=utf-8");

        JSONObject object = new JSONObject();
        try {
            object.put("sign_type", "device_sign");
            object.put("sign_code", "device_code");
            object.put("device_sn", deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi()+"/device-login");

        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败"+e.getMessage());
                EventBus.getDefault().post("网络请求失败");
                stopMedie();
                soundPool.play(musicId.get(4), 1, 1, 0, 0, 1);
            }
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("AllConnects", "请求成功"+call.request().toString());
                //获得返回体
                String ss=null;
                try{
                    ResponseBody body = response.body();
                    ss=body.string().trim();
                    Log.d("AllConnects", "token:"+ss);
                    JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
                    Gson gson = new Gson();
                    logingBean = gson.fromJson(jsonObject, LogingBean.class);
                    if (logingBean.getCode()==0 && logingBean.isSuccess()){
                        //登录成功
                        float score=logingBean.getScore();
                      //  float a= (score-80)==0?0: (float) ((score-80) / 25.0);
                        baoCunBean.setShibieFaZhi(score);
                        baoCunBean.setHuoTi(logingBean.isAlive());
                        //  baoCunBean.setHuoTi(false);
                        baoCunBean.setXgToken(logingBean.getToken());
                        baoCunBeanDao.put(baoCunBean);
                        baoCunBean=baoCunBeanDao.get(123456);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Log.d("MianBanJiActivity3", "初始化sdk");
                                    FacePassHandler.initSDK(getApplicationContext());
                                    FacePassUtil util = new FacePassUtil();
                                    util.init(MianBanJiActivity3.this, getApplicationContext(), SettingVar.faceRotation, baoCunBean);
                                }catch (Exception e){
                                    Log.d("MianBanJiActivity3", e.getMessage()+"");
                                }
                            }
                        });

                        //连接mq
                        factory = new ConnectionFactory();
                        // "guest"/"guest" by default, limited to localhost connections
                        factory.setUsername(logingBean.getRabbitmq().getLogin());
                        factory.setPassword(logingBean.getRabbitmq().getPassword());
                        factory.setVirtualHost(logingBean.getRabbitmq().getVhost());
                        factory.setHost(logingBean.getRabbitmq().getHost());
                        factory.setPort(logingBean.getRabbitmq().getPort());
                        factory.setAutomaticRecoveryEnabled(true);// 设置连接恢复


                        conn = factory.newConnection();
                        channel = conn.createChannel();
                        //声明一个队列
                        channel.queueDeclare(logingBean.getRabbitmq().getQueue(), logingBean.getRabbitmq().getExchange().isDurable(), false, false, null);
                        Consumer consumer = new DefaultConsumer(channel) {
                            @Override
                            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                                String message = new String(body, StandardCharsets.UTF_8);
                                System.out.println("MianBanJiActivity3 [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
                                try {
                                    JsonObject jsonObject2 = GsonUtil.parse(message).getAsJsonObject();
                                    Gson gson2 = new Gson();
                                    AddFacesBean addFacesBean = gson2.fromJson(jsonObject2, AddFacesBean.class);
                                    getPerosnPath=addFacesBean.getPath();
                                    perosnNotifyPath=addFacesBean.getNotify();
                                    if (addFacesBean.getMethod().equals("sync-person")){
                                        //新增。修改
                                        link_add(addFacesBean,1);
                                    }else if (addFacesBean.getMethod().equals("delete-person")){
                                        //删除
                                        link_add(addFacesBean,2);
                                    }
                                }catch (Exception e){
                                    EventBus.getDefault().post("MQ消息解析失败"+e.getMessage());
                                }
//{"method":"sync-person","person_list":["Member_18443015"],"path":"http://113.92.35.143:9001/person-list","notify":"http://113.92.35.143:9001/person-notify","params":{"Hid":"","Ids":["Member_18443015"]}}'
                            }
                        };
                        channel.basicConsume(logingBean.getRabbitmq().getQueue(), true, consumer);
                        Log.d("MianBanJiActivity3", "channel.isOpen():" + channel.isOpen());

                        //    String message="{\"method\":\"sync-person\",\"person_list\":[\"Member_18443015\"],\"path\":\"http://21n2c53681.iask.in:9001/person-list\",\"notify\":\"http://21n2c53681.iask.in:9001/person-notify\",\"params\":{\"Hid\":\"\",\"Ids\":[\"Member_18443015\"]}}\n";
//                        try {
//                            JsonObject jsonObject2 = GsonUtil.parse(message).getAsJsonObject();
//                            Gson gson2 = new Gson();
//                            AddFacesBean addFacesBean = gson2.fromJson(jsonObject2, AddFacesBean.class);
//                            getPerosnPath=addFacesBean.getPath();
//                            perosnNotifyPath=addFacesBean.getNotify();
//                            if (addFacesBean.getMethod().equals("sync-person")){
//                                //新增。修改
//                                link_add(addFacesBean,1);
//                            }else if (addFacesBean.getMethod().equals("delete-person")){
//                                //删除
//                                link_add(addFacesBean,2);
//                            }
//
//                        }catch (Exception e){
//                            EventBus.getDefault().post("MQ消息解析失败"+e.getMessage());
//                        }

                    }else {
                        //登录失败
                        EventBus.getDefault().post("登录失败");
                    }

                }catch (Exception e){
                    EventBus.getDefault().post("登录失败"+e.getMessage());
                    Log.d("WebsocketPushMsg", e.getMessage()+"ttttt");
                }

            }
        });
    }


    //登录
    private void link_loging_40004(String deviceId){
        if (baoCunBean.getHoutaiDiZhi()==null || baoCunBean.getHoutaiDiZhi().equals("")){
            EventBus.getDefault().post("后台地址不正确");
            return;
        }
        final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
        JSONObject object = new JSONObject();
        try {
            object.put("sign_type", "device_sign");
            object.put("sign_code", "device_code");
            object.put("device_sn", deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi()+"/device-login");

        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败"+e.getMessage());
                EventBus.getDefault().post("网络请求失败");
                stopMedie();
                soundPool.play(musicId.get(4), 1, 1, 0, 0, 1);
            }
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("AllConnects", "请求成功"+call.request().toString());
                //获得返回体
                String ss=null;
                try{
                    ResponseBody body = response.body();
                    ss=body.string().trim();
                    Log.d("AllConnects", "token:"+ss);
                    JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
                    Gson gson = new Gson();
                    LogingBean logingBean = gson.fromJson(jsonObject, LogingBean.class);
                    Log.d("MianBanJiActivity3", "logingBean.isSuccess():" + logingBean.isSuccess());
                    if (logingBean.getCode()==0 && logingBean.isSuccess()){
                        //登录成功
                        float score=logingBean.getScore();
                        //float a= (score-80)==0?0: (float) ((score-80) / 25.0);
                        baoCunBean.setShibieFaZhi(score);
                        baoCunBean.setHuoTi(logingBean.isAlive());
                        baoCunBean.setXgToken(logingBean.getToken());
                        baoCunBeanDao.put(baoCunBean);
                        baoCunBean=baoCunBeanDao.get(123456);

                    }else {
                        //登录失败
                        EventBus.getDefault().post("登录失败");
                    }
                }catch (Exception e){
                    EventBus.getDefault().post("登录失败"+e.getMessage());
                    Log.d("WebsocketPushMsg", e.getMessage()+"ttttt");
                }

            }
        });
    }

    //新增人员
    private void link_add(AddFacesBean deviceId,int type){
        if (getPerosnPath==null){
            EventBus.getDefault().post("后台地址不正确");
            return;
        }
        final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
        JSONObject object = new JSONObject();
        JSONObject objectParams = new JSONObject();
        JSONObject object_put = new JSONObject();
        try {
            object.put("device_sn", baoCunBean.getTuisongDiZhi()+"");
            object.put("token", baoCunBean.getXgToken()+"");
            object.put("Hid", deviceId.getParams().getHid());
            JSONArray array=new JSONArray();
            for (String s:deviceId.getParams().getIds()){
                array.put(s);
            }
            object.put("Ids",array);

            object_put.put("Hid", deviceId.getParams().getHid());
            object_put.put("Ids",array);
            objectParams.put("params",object_put);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("MianBanJiActivity3", "objectParams:" + objectParams);
        Log.d("AllConnects", deviceId.getPath()+"\n"+object.toString());
        if (type==2){
            ZhiLingBean logingBean = new ZhiLingBean();
            logingBean.setJsonObject(objectParams);
            logingBean.setCode(-99999);
            try {
                linkedBlockingQueue.put(logingBean);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(getPerosnPath);

        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败"+e.getMessage());
                EventBus.getDefault().post("网络请求失败");
                stopMedie();
                soundPool.play(musicId.get(4), 1, 1, 0, 0, 1);
            }
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("AllConnects", "请求成功"+call.request().toString());
                //获得返回体
                String ss=null;
                try{
                    ResponseBody body = response.body();
                    ss=body.string().trim();
                    Log.d("AllConnects", "token:"+ss);
                    JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
                    Gson gson = new Gson();
                    ZhiLingBean logingBean = gson.fromJson(jsonObject, ZhiLingBean.class);
                    logingBean.setJsonObject(objectParams);
                    if (logingBean.getCode()==0 && logingBean.isSuccess()){
                        linkedBlockingQueue.put(logingBean);

                    }else {
                        //登录失败
                        EventBus.getDefault().post("新增人员失败");
                    }
                }catch (Exception e){
                    EventBus.getDefault().post("新增人员失败"+e.getMessage());
                }

            }
        });
    }

    //获取ic卡信息
    private void link_ic_info(String ic_card){
        if (baoCunBean.getHoutaiDiZhi()==null){
            EventBus.getDefault().post("后台地址不正确");
            isLink=false;
            group_name="facepasstestx";
            return;
        }
        final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
        JSONObject object = new JSONObject();
        try {
            object.put("device_sn", baoCunBean.getTuisongDiZhi()+"");
            object.put("token", baoCunBean.getXgToken()+"");
            object.put("ic_card", ic_card);
        } catch (JSONException e) {
            e.printStackTrace();
            isLink=false;
            group_name="facepasstestx";
        }
        Log.d("MianBanJiActivity3", "objectParams:" + object.toString());
        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi()+"/ic-persons");

        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败"+e.getMessage());
                EventBus.getDefault().post("网络请求失败");
                stopMedie();
                soundPool.play(musicId.get(4), 1, 1, 0, 0, 1);
                isLink=false;
                group_name="facepasstestx";
            }
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("AllConnects", "请求成功"+call.request().toString());
                //获得返回体
                String ss=null;
                try{
                    ResponseBody body = response.body();
                    ss=body.string().trim();
                    Log.d("AllConnects", "ic卡信息:"+ss);
                    JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
                    Gson gson = new Gson();
                    IcCardInfosBean icCardInfosBean = gson.fromJson(jsonObject, IcCardInfosBean.class);
                    if (icCardInfosBean.getCode()==0 && icCardInfosBean.isSuccess()){
                        //语音请刷脸
                        stopMedie();
                        soundPool.play(musicId.get(3), 1, 1, 0, 0, 1);
                        //开始收集信息换掉grouname
                        group_name="facepasstestx2";
                        paAccessControl.reset();
                        isLink=true;
                        new Thread(new Runnable() {
                            boolean f=true;
                            int i =0;
                            @Override
                            public void run() {
                                while (f){
                                    Log.d("TanChuangThread", "cardBitmap:" + cardBitmap);
                                    if (cardBitmap!=null){
                                        Bitmap bitmap_c = BitmapUtil.rotateBitmap(cardBitmap, SettingVar.msrBitmapRotation);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                faceImage.setImageBitmap(bitmap_c);
                                            }
                                        });
                                        float io = 0;
                                        for (IcCardInfosBean.PersonListBean personListBean : icCardInfosBean.getPerson_list()) {
                                            Bitmap bitmap = null;
                                            try {
                                                if (personListBean.getFace_list() != null && personListBean.getFace_list().get(0) != null && !personListBean.getFace_list().get(0).getImg_url().equals("")) {
                                                    bitmap = Glide.with(MianBanJiActivity3.this).asBitmap()
                                                            .load(personListBean.getFace_list().get(0).getImg_url())
                                                            // .sizeMultiplier(0.5f)
                                                            .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                                            .get();
                                                } else {
                                                    Log.d("TanChuangThread", "继续");
                                                    continue;
                                                }
                                                if (bitmap != null) {//有图片
                                                    BitmapUtil.saveBitmapToSD(bitmap, MyApplication.SDPATH3, "aaabbbccc.jpg");
                                                   // File filef = new File(MyApplication.SDPATH3 + File.separator + "aaabbbccc.png");
                                                    Log.d("TanChuangThread", "未压缩前:filef.length():");
                                                    File file = Luban.with(MianBanJiActivity3.this)
                                                            .load(MyApplication.SDPATH3 + File.separator + "aaabbbccc.jpg")
                                                            .ignoreBy(600)
                                                            .setTargetDir(MyApplication.SDPATH3 + File.separator)
                                                            .get(MyApplication.SDPATH3 + File.separator + "aaabbbccc.jpg");

                                                    if (file != null) {
                                                        Log.d("TanChuangThread", file.getAbsolutePath());
                                                       Bitmap bb= BitmapFactory.decodeFile(file.getAbsolutePath());
                                                       Log.d("TanChuangThread", "bb.getWidth():" + bb.getWidth());
                                                        FacePassCompareResult result= paAccessControl.compare(bitmap_c,BitmapFactory.decodeFile(file.getAbsolutePath()),false);
                                                        io =result.score;
                                                        Log.d("TanChuangThread", "io:" + io);
                                                        Log.d("MianBanJiActivity3", "file.delete():" + file.delete());
                                                        if (result.score>=72){//通过
                                                            link_shangchuanshualian(personListBean.getPerson_id(), bitmap_c,result.score+"","0",personListBean.getPerson_type(),personListBean.getPerson_name());
                                                            Log.d("TanChuangThread", "bb():" + bb.getWidth());
                                                            break;//退出里面循环
                                                        }
                                                    }else {
                                                        Log.d("TanChuangThread", "图片压缩失败");
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        Log.d("TanChuangThread", "io2:" + io);
                                        if (io<72){
                                            if (!DengUT.isOPENRed) {
                                                DengUT.isOPENRed = true;
                                                DengUT.getInstance(baoCunBean).openRed();
                                            }
                                            link_shangchuanshualian("", bitmap_c,io+"","1","5","");
                                            break;
                                        }else {//退出外面循环
                                            break;
                                        }
                                    }else {
                                        i++;
                                        SystemClock.sleep(600);
                                        Log.d("TanChuangThread", "加1");
                                        if (i>=20){
                                            f=false;
                                            isLink=false;
                                            group_name="facepasstestx";
                                            Log.d("TanChuangThread", "重置");
                                        }
                                    }
                                }


                            }
                        }).start();

                    }else {
                        //登录失败
                        EventBus.getDefault().post("获取ic卡号对应信息成功失败");
                        isLink=false;
                        group_name="facepasstestx";
                    }
                }catch (Exception e){
                    EventBus.getDefault().post("获取ic卡号对应信息成功失败"+e.getMessage());
                    isLink=false;
                    group_name="facepasstestx";
                }

            }
        });
    }

    //同步人员
    private void link_sync_person(List<String> successfulList, List<FailedPersonBean> failedPersonBeanList, JSONObject jsonObject){
        if (perosnNotifyPath==null){
            EventBus.getDefault().post("后台地址不正确");
            return;
        }
        final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
        JSONObject object = new JSONObject();
        try {
            object.put("device_sn", baoCunBean.getTuisongDiZhi()+"");
            object.put("token", baoCunBean.getXgToken()+"");
            // object.put("Hid", deviceId.getParams().getHid());
            JSONArray s_array=new JSONArray();
            for (String s:successfulList){
                s_array.put(s);
            }
            object.put("successful",s_array);
            ///////////////////////////////////////////////
            JSONArray f_array=new JSONArray();
            for (FailedPersonBean f:failedPersonBeanList){
                JSONObject fo = new JSONObject();
                fo.put("person_id",f.getPerson_id());
                fo.put("reason",f.getReason());
                f_array.put(fo);
            }
            object.put("failed",f_array);
            object.put("params",jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("提交新增人员状态", perosnNotifyPath+"\n"+object.toString());
        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(perosnNotifyPath);
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败"+e.getMessage());
                EventBus.getDefault().post("网络请求失败");
                stopMedie();
                soundPool.play(musicId.get(4), 1, 1, 0, 0, 1);
            }
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("AllConnects", "请求成功"+call.request().toString());
                //获得返回体
                String ss=null;
                try{
                    ResponseBody body = response.body();
                    ss=body.string().trim();
                    Log.d("AllConnects", "人员同步:"+ss);
                    JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
                    Gson gson = new Gson();
                    LogingBean logingBean = gson.fromJson(jsonObject, LogingBean.class);
                    if (logingBean.getCode()==0 && logingBean.isSuccess()){
                        //登录成功

                    }else {
                        //登录失败
                        EventBus.getDefault().post("同步失败");
                    }

                }catch (Exception e){
                    EventBus.getDefault().post("同步失败"+e.getMessage());
                    Log.d("WebsocketPushMsg", e.getMessage()+"ttttt");
                }

            }
        });
    }
    //上传状态
    private void link_updataState(String deviceId){
        if (baoCunBean.getHoutaiDiZhi()==null || baoCunBean.getHoutaiDiZhi().equals("")){
            EventBus.getDefault().post("后台地址不正确");
            return;
        }
        String sdk_version=null;
        if (paAccessControl!=null){
            sdk_version= FacePassHandler.getVersion();
        }else {
            sdk_version="1.0.0";
        }
        final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
        JSONObject object = new JSONObject();
        try {
            object.put("device_sn", deviceId);
            object.put("token", baoCunBean.getXgToken());
            object.put("device_ip", FileUtil.getIPAddress(getApplicationContext()));
            object.put("device_name", baoCunBean.getDevice_name());
            object.put("device_status", "1");
            object.put("sdk_version",sdk_version );
            object.put("cpu_usage", GetCpuState.getRate());
            object.put("memory_size",GetCpuState.getTotalMemory(MianBanJiActivity3.this));
            object.put("memory_usage", GetCpuState.getAvailMemory(MianBanJiActivity3.this));
            object.put("disk_size", GetCpuState.getTotalInternalMemorySize()+"");
            object.put("disk_usage",(Float.parseFloat(GetCpuState.getAvailableInternalMemorySize())/Float.parseFloat(GetCpuState.getTotalInternalMemorySize()))+"");//磁盘使用率
            object.put("disk_free_size", GetCpuState.getAvailableInternalMemorySize()+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("MianBanJiActivity3", object.toString());
        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi()+"/device-notify");

         OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .writeTimeout(18000, TimeUnit.MILLISECONDS)
                .connectTimeout(18000, TimeUnit.MILLISECONDS)
                .readTimeout(18000, TimeUnit.MILLISECONDS)
//				    .cookieJar(new CookiesManager())
                //        .retryOnConnectionFailure(true)
                .build();
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败"+e.getMessage());
                EventBus.getDefault().post("网络请求失败");
                stopMedie();
                soundPool.play(musicId.get(4), 1, 1, 0, 0, 1);
            }
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("AllConnects", "请求成功"+call.request().toString());
                //获得返回体
                String ss=null;
                try{
                    ResponseBody body = response.body();
                    ss=body.string().trim();
                    Log.d("MianBanJiActivity3", "token:"+ss);
                    JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
                    Gson gson = new Gson();
                    TimeStateBean logingBean = gson.fromJson(jsonObject, TimeStateBean.class);
                    if (logingBean.getCode()==0){
                        //提交状态成功
                        Log.d("MianBanJiActivity3", "成功提交状态");
                    }else if (logingBean.getCode()==40004){
                        //登录失败
                        link_loging_40004(baoCunBean.getTuisongDiZhi());
                    }else {
                        EventBus.getDefault().post("提交状态失败");
                    }
                }catch (Exception e){
                    EventBus.getDefault().post("提交状态失败"+e.getMessage());
                    Log.d("WebsocketPushMsg", e.getMessage()+"ttttt");
                }

            }
        });
    }


    //上传记录
    private void link_shangchuanshualian(String person_id, Bitmap bitmap,String capture_score,String capture_status,String person_type,String name) {
        if (baoCunBean.getHoutaiDiZhi() == null || baoCunBean.getHoutaiDiZhi().equals("")) {
            isLink=false;
            group_name="facepasstestx";
            return;
        }
        // Bitmap bb = BitmapUtil.rotateBitmap(bitmap, SettingVar.msrBitmapRotation);
        final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
        JSONObject object = new JSONObject();
        try {
            object.put("device_sn", baoCunBean.getTuisongDiZhi());
            object.put("token", baoCunBean.getXgToken());
            object.put("person_id", person_id);
            object.put("person_name", baoCunBean.getXgToken());
            object.put("person_type",person_type);
            object.put("capture_img", BitmapUtil.bitmapToBase64(bitmap));
            object.put("capture_time", DateUtils.tim(System.currentTimeMillis()+""));
            object.put("capture_score", capture_score);
            object.put("capture_status", capture_status);//0 ：通过，白名单比对成功时传        1 ：不通过，用于陌生人采集场景
        } catch (JSONException e) {
            e.printStackTrace();
            isLink=false;
            group_name="facepasstestx";
        }
        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi() + "/attendance-record");
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败" + e.getMessage());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "网络请求失败", TastyToast.LENGTH_LONG, TastyToast.INFO);
//                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                        tastyToast.show();
//                    }
//                });
                stopMedie();
                soundPool.play(musicId.get(4), 1, 1, 0, 0, 1);
                isLink=false;
                group_name="facepasstestx";
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();
                    Log.d("AllConnects", "上传识别记录" + ss);
                    JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
                    Gson gson = new Gson();
                    OpenDoorBean openDoorBean = gson.fromJson(jsonObject, OpenDoorBean.class);
                    if (openDoorBean.isOutput()){//开门
                        stopMedie();
                        soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
                        DengUT.getInstance(baoCunBean).openDool();
                        showUIResult(4,name,"认证通过");
                        closeDoor();//延时几秒后关门
                        if (!DengUT.isOPENGreen) {
                            DengUT.isOPENGreen = true;
                            DengUT.getInstance(baoCunBean).openGreen();
                        }
                        isLink=false;
                        group_name="facepasstestx";
                    }else {
                        stopMedie();
                        soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
                        isLink=false;
                        group_name="facepasstestx";
                        showUIResult(3,"陌生人","");
                        DengUT.isOPEN = true;
                    }
                } catch (Exception e) {
                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
                    isLink=false;
                    group_name="facepasstestx";
                    stopMedie();
                    soundPool.play(musicId.get(5), 1, 1, 0, 0, 1);
                    showUIResult(3,"陌生人","");
                    DengUT.isOPEN = true;
                }
            }
        });
    }

}
