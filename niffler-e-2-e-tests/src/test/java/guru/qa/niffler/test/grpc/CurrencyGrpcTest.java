package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CurrencyGrpcTest extends BaseGrpcTest {

    @Test
    void allCurrenciesShouldReturned() {
        final CurrencyResponse response = CURRENCY_BLOCKING_STUB.getAllCurrencies(Empty.getDefaultInstance());
        final List<Currency> allCurrenciesList = response.getAllCurrenciesList();
        Assertions.assertEquals(4, allCurrenciesList.size());
    }

    static Stream<Arguments> calculatedRateDataProvider() {
        return Stream.of(
                Arguments.arguments(CurrencyValues.USD, 1000, 15),
                Arguments.arguments(CurrencyValues.KZT, 3000, 21428.57),
                Arguments.arguments(CurrencyValues.EUR, 1000, 13.89)
        );
    }

    @ParameterizedTest
    @MethodSource("calculatedRateDataProvider")
    void shouldCalculateAmountForSpendsInRUB(CurrencyValues currencyTarget, double amount, double expected) {
        final CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(currencyTarget)
                .build();
        final CalculateResponse response = CURRENCY_BLOCKING_STUB.calculateRate(request);
        Assertions.assertEquals( expected,response.getCalculatedAmount());
    }

    static Stream<Arguments> calculatedRateForMinSpendDataProvider() {
        return Stream.of(
                Arguments.arguments(CurrencyValues.USD, 0.0),
                Arguments.arguments(CurrencyValues.KZT, 0.07),
                Arguments.arguments(CurrencyValues.EUR, 0.0)
        );
    }

    @Test
    @DisplayName("GRPC -> Currency - проверяем getAllCurrencies")
    void grpc_allCurrenciesShouldReturned() {
        final CurrencyResponse response = CURRENCY_BLOCKING_STUB.getAllCurrencies(Empty.getDefaultInstance());
        final List<Currency> allCurrenciesList = response.getAllCurrenciesList();

        assertEquals(4, allCurrenciesList.size());
    }

    @ParameterizedTest
    @MethodSource("calculatedRateDataProvider")
    @DisplayName("GRPC -> Currency - проверяем calculateRate")
    void grpc_shouldCalculateAmountForSpendsInRUB(CurrencyValues targetCurrency, double amount, double expectedValue) {
        final CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(targetCurrency)
                .build();

        final CalculateResponse calculateResponse = CURRENCY_BLOCKING_STUB.calculateRate(request);
        assertEquals(expectedValue, calculateResponse.getCalculatedAmount());
    }

    @ParameterizedTest
    @MethodSource("calculatedRateForMinSpendDataProvider")
    @DisplayName("GRPC -> Currency - проверяем calculateRate for min spends")
    void grpc_shouldCalculateAmountForMinSpendsInRUB(CurrencyValues targetCurrency, double expectedValue) {
        final double amount = 0.01;
        final CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(targetCurrency)
                .build();

        final CalculateResponse calculateResponse = CURRENCY_BLOCKING_STUB.calculateRate(request);
        assertEquals(expectedValue, calculateResponse.getCalculatedAmount());
    }
}
