package mat_kap_calc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MatCapCalc_Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField apartment_cost_t;

    @FXML
    private TextField matCap_cost_t;

    @FXML
    private TextField count_family_t;

    @FXML
    private Button sumCap_b;

    @FXML
    private TextArea result_ta;

    @FXML
    private TextField count_parent_t;

    @FXML
    void initialize() {

        // Запрет на ввод символов, кроме цифр. Деление на разряды чисел.(по 3)
        setSpaceDigits(apartment_cost_t);
        setSpaceDigits(matCap_cost_t);
        setSpaceDigits(count_family_t);
        setSpaceDigits(count_parent_t);

        // Событие на кнопку
        sumCap_b.setOnAction(event -> {
            // Проверка на пустые поля. Если есть хотя бы одно пустое поле, то вывести сообщение.
            if (apartment_cost_t.getText().isEmpty() || matCap_cost_t.getText().isEmpty() || count_family_t.getText().isEmpty() || count_parent_t.getText().isEmpty() ){
                // Сообщение об ошибке
                Alert alert =new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Одно из полей не заполнено!");
                alert.setContentText("Заполните каждое поле!");

                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK){
                        //System.out.println("Pressed ok");
                    }
                });
            } else {
                // Иначе удаляем все пробелы в строках
                //get data from textfield and parse to double
                double apart_cost = Double.parseDouble(apartment_cost_t.getText().replaceAll("\\s", ""));
                double matCap_cost = Double.parseDouble(matCap_cost_t.getText().replaceAll("\\s", ""));
                double count_family = Double.parseDouble(count_family_t.getText().replaceAll("\\s", ""));
                double count_parent = Double.parseDouble(count_parent_t.getText().replaceAll("\\s", ""));
                //System.out.println(share_calc(apart_cost, matCap_cost, count_family,count_parent));
                // Если стоимость квартиры < Материнского капитала
                if (apart_cost<=matCap_cost){
                    // Выводим ошибку
                    Alert alert =new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Стоимость квартиры не может быть меньше материнского капитала!");
                    alert.setContentText("Укажите другие данные!");

                    alert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK){
                            //System.out.println("Pressed ok");
                        }
                    });
                } else {
                    // Иначе рассчитываем долю на каждого члена семьи
                    double prava_child=share_calc(apart_cost, matCap_cost, count_family,count_parent);
                    double part_every_child=matCap_cost/count_family; // Получаем  минимальную долю на каждого ребенка

                    // Округляем
                    BigDecimal part_every_child_bd = new BigDecimal(part_every_child);
                    part_every_child_bd = part_every_child_bd.setScale(3, RoundingMode.HALF_UP);
                    part_every_child=Double.parseDouble(part_every_child_bd.toString());

                    BigDecimal prava_child_bd = new BigDecimal(prava_child);
                    prava_child_bd = prava_child_bd.setScale(3, RoundingMode.HALF_UP);
                    prava_child=Double.parseDouble(prava_child_bd.toString());

                    // Форматируем числа под формат ru
                    String part_every_child_str=formatNumber(part_every_child);
                    String prava_child_str=formatNumber(prava_child);
                    // Проверяем, нарушены ли права ребенка
                    if (prava_child>=part_every_child){
                        Alert alert =new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Информация");
                        alert.setHeaderText("Права ребёнка не нарушены!");
                        alert.setContentText("Каждый ребёнок получает больше доли материнского капитала. Права ребёнка не нарушены!\n" +
                                "\nУ детей должно быть не меньше: "+part_every_child_str+" руб."+
                                "\nНа каждого ребёнка приходится: "+prava_child_str+" руб.");

                        alert.showAndWait().ifPresent(rs -> {
                            if (rs == ButtonType.OK){
                                //System.out.println("Pressed ok");
                            }
                        });
                    } else {
                        Alert alert =new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Информация");
                        alert.setHeaderText("Права ребенка нарушены!");
                        alert.setContentText("Каждый ребенок получает меньше доли материнского капитала. Права ребенка нарушены!\n" +
                                "\nУ детей должно быть не меньше: "+part_every_child+" руб."+
                                "\nНа каждого ребенка приходится: "+prava_child+" руб.");

                        alert.showAndWait().ifPresent(rs -> {
                            if (rs == ButtonType.OK){
                                //System.out.println("Pressed ok");
                            }
                        });
                    }
                }
            }
        });
    }

    // Функция для проверки на цифры (не используется, т.к. такой функционал есть в функции setSpaceDigits)
    public boolean isOnlyDigits(String str) {
        return str.matches("[\\d]+");
    }

    // Функция для форматирования чисел
    public String formatNumber(Double number_ch){
        Locale loc = new Locale("ru");
        NumberFormat formatter = NumberFormat.getInstance(loc);
        String result = formatter.format(number_ch);
        //System.out.println(loc.getLanguage() + ": " + result);
        return result;
    }

    // Функция для удаления всех лишних символов, кроме цифр. А также функция делит число на разряды.
    public void setSpaceDigits(TextField textField){
        final char seperatorChar = ' '; // Устанавливаем разделитель для разряда
        // Устанавливаем паттерн для регулярных выражений. Реглярные выражения проверяют наличие цифр
        final Pattern p = Pattern.compile("[0-9" + seperatorChar + "]*");
        // Устнавливам формат для текстового поля
        textField.setTextFormatter(new TextFormatter<>(c -> {
            if (!c.isContentChange()) {
                return c; // Если нет изменений в текстовом поле
            }
            String newText = c.getControlNewText();
            if (newText.isEmpty()) {
                return c;
            }
            if (!p.matcher(newText).matches()) {
                return null; // Если не нашлось совпадений с паттерном
            }

            // Разворачиваем строку
            int suffixCount = c.getControlText().length() - c.getRangeEnd();
            int digits = suffixCount - suffixCount / 4;
            StringBuilder sb = new StringBuilder();

            // Проверяем на разряды. Если более 3 цифр подряд, то ставим разделитель
            if (digits % 3 == 0 && digits > 0 && suffixCount % 4 != 0) {
                sb.append(seperatorChar);
            }

            // Добавляем остальные цифры в обратном порядке
            for (int i = c.getRangeStart() + c.getText().length() - 1; i >= 0; i--) {
                char letter = newText.charAt(i);
                if (Character.isDigit(letter)) {
                    sb.append(letter);
                    digits++;
                    if (digits % 3 == 0) {
                        sb.append(seperatorChar);
                    }
                }
            }

            // Удаляем разделитель, если последняя цифра
            if (digits % 3 == 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.reverse();
            int length = sb.length();

            // Заменяем модифицированным текстом
            c.setRange(0, c.getRangeEnd());
            c.setText(sb.toString());
            c.setCaretPosition(length);
            c.setAnchor(length);

            return c;
        }));
    }

    // Функция для подсчета долей для материнского капитала
    public double share_calc(double apart_cost, double matCap_cost, double count_family, double count_parent){
        // Размер мат.капитала делим на стоимость квартиры и на кол-во членов семьи
        double share_every_child=matCap_cost/apart_cost/count_family;
        double share_every_parent=0.00;
        double difference_m=apart_cost-matCap_cost;
        double difference_part=0.00;
        // Округляем долю на каждого члена семьи в верхнюю сторону
        BigDecimal every_child = new BigDecimal(share_every_child);
        every_child = every_child.setScale(3, RoundingMode.UP);
        double every_child_double=Double.parseDouble(every_child.toString());

        // Проверяем права ребенка
        double prava_child=0.0;
        prava_child=apart_cost*every_child_double;

        // Считаем долю родителей
        share_every_parent=1-(every_child_double*(count_family-count_parent));
        BigDecimal every_parent = new BigDecimal(share_every_parent);
        //every_parent = BigDecimal.valueOf(0.4974);
        // Округляем
        every_parent = every_parent.setScale(3, RoundingMode.HALF_UP);
        double every_parent_double=Double.parseDouble(every_parent.toString());
        String text_res_child="";
        String text_res_parent="";

        // Выодим результаты в текстовую область
        for (int i=1; i<=count_family-count_parent; i++){
            //text_res_child+="Доля ребенка "+i+": "+every_child_double+" (Без округления: "+share_every_child+")"+"\n";
            text_res_child+="Доля ребенка "+i+": "+every_child_double+" ("+Math.round(every_child_double*1000)+"/1000)"+"\n";
        }
        if (count_parent==1){
            //text_res_parent="Разница между капиталом и суммой квартиры: "+Math.round(difference_m)+"\n";
            //text_res_parent="Доля родителя 1: "+every_parent_double+" (Без округления: "+share_every_parent+")"+"\n";
            text_res_parent+="Доля родителя 1: "+every_child_double+" ("+Math.round(every_child_double*1000)+"/1000)"+"\n";
        } else {
            //text_res_parent="Разница между капиталом и суммой квартиры: "+Math.round(difference_m)+"\n";
            for (int i=1; i<= count_parent; i++){
                //text_res_parent+="Доля родителя "+i+": "+every_parent_double/2+" (Без округления: "+share_every_parent/2+")"+"\n";
                text_res_parent+="Доля родителя "+i+": "+every_child_double+" ("+Math.round(every_child_double*1000)+"/1000)"+"\n";
            }
        }
        // Разница между капиталом и суммой квартиры
        difference_part=1-(every_child_double*count_family);
        BigDecimal difference_part_bd = new BigDecimal(difference_part);
        difference_part_bd = difference_part_bd.setScale(3, RoundingMode.HALF_UP);
        double difference_part_double=Double.parseDouble(difference_part_bd.toString());
        String difference_m_str=formatNumber(difference_m);
        //result_ta.setText("Разница между капиталом и суммой квартиры: "+Math.round(difference_m)+
        // Выводим результат в текстовую область
        result_ta.setText("Разница между капиталом и суммой квартиры: "+difference_m_str+" руб."+
                "\nДоля членов семьи:\n"+text_res_child+text_res_parent+"\n"+
                "Доля родителей: "+difference_part_double+" ("+Math.round(difference_part_double*1000)+"/1000)");

        return prava_child;
    }
}