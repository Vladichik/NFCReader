package lioha.clock;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by vladvidavsky on 6/6/16.
 */
public class DataSender {
    public static ToastCustom tc;

    public DataSender(Activity activity) {
        tc = new ToastCustom(activity);
    }

    private static RequestBody body;

    /**
     * Метод основная цель которого создать и приобразовать данные
     * для отправки на сервак.
     *
     * @param type     стринг который указывает режим пользователя если пользователь пришел как нигретянский раб на работу или уебал с нее.
     * @param badgeNum стринг указывает серийный номер нататуированый на лбу раба
     */
    public static void PreparePostParams(String type, String badgeNum) {
        String extraData, job, date, time;
        extraData = "";
        job = "";
        time = DataCollector.GetTimeAndDate("time");
        date = DataCollector.GetTimeAndDate("date");


        /**
         * Создаем параметры в понятном бля для сервака формате.
         * Тут конечно пришлось поебаться чутка, но, с кем не бывает.
         */
        body = new FormBody.Builder()
                .add("type", type)
                .add("badge", badgeNum)
                .add("job", job)
                .add("extraData", extraData)
                .add("time", time)
                .add("date", date)
                .build();

        new SendingDataToServer().execute(Constants.BASE_URL + "/swipe");
    }

    /**
     * Метод отправляющий все данные нахуй на сервак.
     */
    public static class SendingDataToServer extends AsyncTask<String, String, String> {
        OkHttpClient client = new OkHttpClient();

        protected String doInBackground(String... params) {
            Request request = new Request.Builder()
                    .url(params[0])
                    .post(body)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response != null) {

                String stringResp = "no data received";
                try {
                    stringResp = String.valueOf(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                response.body().close();
                return stringResp;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String value) {
            MainActivity.preloader.setVisibility(View.GONE);
            if (value != null) {
                Boolean black = value.contains("black");
                Boolean green = value.contains("green");
                Boolean red = value.contains("red");
                String responseString = DataCollector.GetServerResponseMessage(value);
                if (green) {
                    tc.showCustomToast(3, responseString);
                } else if (red) {
                    tc.showCustomToast(4, responseString);
                } else if (black) {
                    tc.showCustomToast(5, responseString);
                }
            } else {
                tc.showCustomToast(7, null);
            }
        }

    }

}
