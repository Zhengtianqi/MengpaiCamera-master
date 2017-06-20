package com.stickercamera.app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.skykai.stickercamera.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends Activity {
    EditText etName,etPwd;
    Button loginbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //取消顶部标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etName=(EditText)findViewById(R.id.r_username);
        etPwd=(EditText)findViewById(R.id.r_userpassword);
        loginbtn=(Button)findViewById(R.id.register);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {//登陆按钮监听事件
/*                ((App)getApplicationContext()).setTextData(et.getText().toString());
                location_x.setText(((App)getApplicationContext()).getTextData());*/
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int result = reg();
                            //login()为向php服务器提交请求的函数，返回数据类型为int
                            if (result == 1) {
                                Log.e("log_tag", "注册成功！");
                                //Toast toast=null;
                                Looper.prepare();
                                Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            } else if (result == -2) {
                                Log.e("log_tag", "用户已经存在！");
                                //Toast toast=null;
                                Looper.prepare();
                                Toast.makeText(RegisterActivity.this, "用户已经存在！", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }).start();
            }
        });
    }

    //login-------------------------
        /*
    *用户登录提交post请求
    * 向服务器提交数据1.user_id用户名，2.input_pwd密码
    * 返回JSON数据{"status":"1","info":"login success","sex":"0","nicename":""}
    */
    private int reg() throws IOException {
        int returnResult=0;
        /*获取用户名和密码*/
        String user_id=etName.getText().toString();
        String input_pwd=etPwd.getText().toString();
        if(user_id==null||user_id.length()<=0){
            Looper.prepare();
            Toast.makeText(RegisterActivity.this,"请输入账号", Toast.LENGTH_LONG).show();
            Looper.loop();
            return 0;

        }
        if(input_pwd==null||input_pwd.length()<=0){
            Looper.prepare();
            Toast.makeText(RegisterActivity.this,"请输入密码", Toast.LENGTH_LONG).show();
            Looper.loop();
            return 0;
        }
        String urlstr="http://10.0.2.2/register.php";
        //建立网络连接
        URL url = new URL(urlstr);
        HttpURLConnection http= (HttpURLConnection) url.openConnection();
        //往网页写入POST数据，和网页POST方法类似，参数间用‘&’连接
        String params="uid="+user_id+'&'+"pwd="+input_pwd;
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        OutputStream out=http.getOutputStream();
        out.write(params.getBytes());//post提交参数
        out.flush();
        out.close();

        //读取网页返回的数据
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(http.getInputStream()));//获得输入流
        String line="";
        StringBuilder sb=new StringBuilder();//建立输入缓冲区
        while (null!=(line=bufferedReader.readLine())){//结束会读入一个null值
            sb.append(line);//写缓冲区
        }
        String result= sb.toString();//返回结果

        try {
            /*获取服务器返回的JSON数据*/
            JSONObject jsonObject= new JSONObject(result);
            returnResult=jsonObject.getInt("status");//获取JSON数据中status字段值
        } catch (Exception e) {
            Log.e("log_tag", "the Error parsing data "+e.toString());
        }
        return returnResult;
    }
    //reg=========================
}
