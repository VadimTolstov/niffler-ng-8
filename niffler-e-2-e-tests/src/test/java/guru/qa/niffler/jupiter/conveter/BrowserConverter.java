package guru.qa.niffler.jupiter.conveter;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.utils.Browser;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class BrowserConverter implements ArgumentConverter {


    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (!(source instanceof Browser browser)) {
            throw new ArgumentConversionException("Source must be a Browser enum");
        }
        SelenideConfig config = (browser == Browser.CHROME)
                ? SelenideUtils.chromeConfig
                : SelenideUtils.firefoxConfig;
        return new SelenideDriver(config);
    }
}