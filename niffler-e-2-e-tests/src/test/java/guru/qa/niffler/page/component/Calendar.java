package guru.qa.niffler.page.component;

import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$;

public class Calendar extends BaseComponent<Calendar> {

    public Calendar() {
        super($("input[name='date']"));
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Step("Выбор дату в календаре: {date}")
    public void selectDateInCalendar(@Nonnull LocalDate date) {
        self.setValue(date.format(formatter));
    }
}
