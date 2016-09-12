package lioha.clock;

import org.jsoup.Jsoup;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by vladvidavsky on 6/6/16.
 */
public class DataCollector {
    /**
     * Метод генерирующий и форматирующий системное время и дату для использования под разные требования
     * @param type Стргинг поступающий при егзекуции метода указывающий как форматироварь дату
     * @return Стринг отформатированная дата
     */
    public static String GetTimeAndDate(String type) {
        long timeInMillis = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
        SimpleDateFormat format = new SimpleDateFormat();
        switch (type) {
            case "time":
                format = new SimpleDateFormat("HH:mm:ss");
                break;
            case "date":
                format = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
                break;
        }
        return format.format(cal.getTime());
    }

    /**
     * Метод превращающий наскальный монускрипт древних мороканцев в понятный для
     * нашей цивилизации UID NFC элемента
     * @param inarray поступающий UID в ебанутом формате
     * @return возвращает UID в человеческом формате
     */
    public static String ByteArrayToHexString(byte [] inarray) {
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";

        for(j = 0 ; j < inarray.length ; ++j)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    /**
     * Функция извлекающая нужный текст из хаотичного HTML стринга
     * приходящего с сервака после отправки номера NFC чипа.
     * @param response ответ сервака
     * @return возвращаем очищеный от HTML тагов текст чтобы показать пользователю
     * все ли ништяк или сервак посал его нахуй.
     */
    public static String GetServerResponseMessage(String response){
        response = response.substring(response.indexOf("<H1>"), response.indexOf("</H1>"));
        return Jsoup.parse(response).text();
    }
}
