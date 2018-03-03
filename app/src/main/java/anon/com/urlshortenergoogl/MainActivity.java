package anon.com.urlshortenergoogl;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import anon.com.urlshortenergoogl.ShortURL.ShortUrlListener;

public class MainActivity extends AppCompatActivity {

    private ClipboardManager myClipboard;
    private ClipData myClip;


    private int progressStatus = 0;
    private Handler handler = new Handler();

    boolean canClick = false;

    Context context;
    TextView textView;

    EditText editText;
    Button button, button2, button4;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);                                      //shortened text
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textView.getText().toString();

                if (text != null && 0 < text.length() && canClick) {

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(text));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        });

        editText = (EditText) findViewById(R.id.editText);
        editText.setText("http://www.google.com");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        button = (Button) findViewById(R.id.button);                                              //short button
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                progressStatus = 0;
                textView.setText("");

                new Thread(new Runnable() {
                    public void run() {                                                     //progress bar
                        while (progressStatus < 100) {
                            progressStatus += 1;
                            handler.post(new Runnable() {
                                public void run() {
                                    progressBar.setProgress(progressStatus);
                                }
                            });
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();


                ShortURL.makeShortUrl(editText.getText().toString(), new ShortUrlListener() {
                    @Override
                    public void OnFinish(String url) {                    // if url field is empty

                        if (url != null && 0 < url.length()) {
                            canClick = true;
                            textView.setText(url);
                        } else {
                            canClick = false;
                            textView.setText("No URL to shorten");
                        }

                    }
                });
            }
        });

        button2 = (Button) findViewById(R.id.button2);                                          //copy button
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        button2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String text;
                text = textView.getText().toString();

                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);

                Toast.makeText(getApplicationContext(), "URL Copied !!",
                        Toast.LENGTH_SHORT).show();
            }


        });


        button4 = (Button) findViewById(R.id.button4);                                              //share button
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                text = textView.getText().toString();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = text;
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });
    }
}




