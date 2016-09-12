package lioha.clock;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by vladvidavsky on 6/18/16.
 */
public class ToastCustom {
    Toast toast;
    Context cont;

    public ToastCustom(Activity activity) {
        cont = activity;
        toast = new Toast(cont);
    }

    /**
     * Данный метод отвечает за показ разного рода оповещений для пользователя
     * в Андроиде данный тип оповещений называется Toast
     * @param type этот параметр определяющий какой Toast показать пользователю
     */
    public void showCustomToast(int type, String toastText) {
        toast.setDuration(Toast.LENGTH_LONG);
        LayoutInflater inflater = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        switch (type) {
            case 1:
                // Этот Тост показывается если юзер полный ебанат кальция и не выбрал Clock In или Clock Out
                view = inflater.inflate(R.layout.toast_error_cutom, null);
                break;
            case 2:
                view = inflater.inflate(R.layout.toast_error_cutom, null);
                break;
            case 3:
                // Данный тоаст появляется когда сервак возвращает ответ с зеленым цветом
                view = inflater.inflate(R.layout.toast_green_cutom, null);
                TextView text = (TextView) view.findViewById(R.id.green_msg);
                text.setText(toastText);
                break;
            case 4:
                // Данный тоаст появляется когда сервак возвращает ответ с красным цветом
                view = inflater.inflate(R.layout.toast_red_cutom, null);
                TextView errorTXT = (TextView) view.findViewById(R.id.red_msg);
                errorTXT.setText(toastText);
                break;
            case 5:
                // Данный тоаст появляется когда сервак возвращает ответ с черным цветом
                view = inflater.inflate(R.layout.toast_black_cutom, null);
                TextView serverRefuseTxt = (TextView) view.findViewById(R.id.black_msg);
                serverRefuseTxt.setText(toastText);
                break;
            case 6:
                // Данный тоаст появляется когда нет интернет подключения при попытке послать данные на сервер
                view = inflater.inflate(R.layout.toast_red_cutom, null);
                TextView noInternetTxt = (TextView) view.findViewById(R.id.red_msg);
                String NOINTERNET = cont.getResources().getString(R.string.no_internet);
                noInternetTxt.setText(NOINTERNET);
                break;
            case 7:
                // Данный тоаст появляется когда нет ответа с сервака, может че то пиздой накрылось
                view = inflater.inflate(R.layout.toast_red_cutom, null);
                TextView noRespTxt = (TextView) view.findViewById(R.id.red_msg);
                String NORESPONSE = cont.getResources().getString(R.string.no_server_resp);
                noRespTxt.setText(NORESPONSE);
                break;
        }
        toast.setView(view);
        toast.show();
    }
}
