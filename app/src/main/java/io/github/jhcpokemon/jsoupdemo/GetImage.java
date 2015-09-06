package io.github.jhcpokemon.jsoupdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jhcpokemon on 09/06/15.
 */
public class GetImage extends Activity {
    @Bind(R.id.image)
    ImageView imageView;
    MyHandler handler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        getBitmap();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    void getBitmap(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri = "http://jakewharton.github.io/butterknife/";
                try {
                    Document doc = Jsoup.connect(uri).get();
                    Elements links = doc.select("img");
                    String imagePath = links.get(0).attr("abs:src");
                    URLConnection connection = new URL(imagePath).openConnection();
                    InputStream in = connection.getInputStream();
                    BufferedInputStream bs = new BufferedInputStream(in);
                    Bitmap bitmap = BitmapFactory.decodeStream(bs);
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,bao);
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("Bitmap",bao.toByteArray());
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    bs.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(msg.peekData().getByteArray("Bitmap"),0,
                    msg.peekData().getByteArray("Bitmap").length));
        }
    }
}
