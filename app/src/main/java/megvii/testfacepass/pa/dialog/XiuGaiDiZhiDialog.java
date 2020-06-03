package megvii.testfacepass.pa.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import megvii.testfacepass.pa.R;


/**
 * @Function: 自定义对话框
 * @Date: 2013-10-28
 * @Time: 下午12:37:43
 * @author Tom.Cai
 */
public class XiuGaiDiZhiDialog extends Dialog {
    private TextView title2;
    private Button l1,l2;
    private EditText jiudianname,idid;
    public XiuGaiDiZhiDialog(Context context) {
        super(context, R.style.dialog_style2);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.xiugaidialog_dizhi, null);

        jiudianname=  mView.findViewById(R.id.xiangce);
        idid= mView.findViewById(R.id.idid);
        title2=  mView.findViewById(R.id.title2);
        l1= mView. findViewById(R.id.queren);
        l2=  mView.findViewById(R.id.quxiao);

        super.setContentView(mView);
    }

    public void setContents(String title,String ss, String s3){
        if (title!=null)
            title2.setText(title);
       if (ss!=null)
           idid.setText(ss);
        if (s3!=null)
            jiudianname.setText(s3);
    }

    public String getUrl(){
        return idid.getText().toString().trim();
    }




    @Override
    public void setContentView(int layoutResID) {

    }

    @Override
    public void setContentView(View view, LayoutParams params) {
    }

    @Override
    public void setContentView(View view) {
    }

    /**
     * 确定键监听器
     * @param listener
     */
    public void setOnQueRenListener(View.OnClickListener listener){
        l1.setOnClickListener(listener);
    }
    /**
     * 取消键监听器
     * @param listener
     */
    public void setQuXiaoListener(View.OnClickListener listener){
        l2.setOnClickListener(listener);
    }


}