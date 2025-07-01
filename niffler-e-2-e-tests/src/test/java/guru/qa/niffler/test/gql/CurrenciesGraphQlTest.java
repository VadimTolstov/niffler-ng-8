package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.CurrenciesQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.GqlTest;
import jaxb.userdata.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CurrenciesGraphQlTest extends BaseGraphQlTest {


    @User
    @Test
    @ApiLogin
    void allCurrenciesShouldBeReturnedFromGraphQl(@Token String bearerToken) {
        final ApolloCall<CurrenciesQuery.Data> currenciesCall = apolloClient.query(new CurrenciesQuery())
                .addHttpHeader("Authorization", "Bearer " + bearerToken);

        final ApolloResponse<CurrenciesQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final CurrenciesQuery.Data data = response.dataOrThrow();
        final List<CurrenciesQuery.Currency> all = data.currencies;
        Assertions.assertEquals(Currency.RUB.name(), all.get(0).currency.rawValue);
        Assertions.assertEquals(Currency.KZT.name(), all.get(1).currency.rawValue);
        Assertions.assertEquals(Currency.EUR.name(), all.get(2).currency.rawValue);
        Assertions.assertEquals(Currency.USD.name(), all.get(3).currency.rawValue);


    }
}
