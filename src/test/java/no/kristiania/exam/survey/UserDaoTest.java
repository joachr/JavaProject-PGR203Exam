package no.kristiania.exam.survey;

import no.kristiania.exam.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;

public class UserDaoTest {

    private final UserDao userDao = new UserDao(TestData.testDataSource());

    @BeforeAll
    static void initSurvey() throws SQLException {
        SurveyDao surveyDao = new SurveyDao(TestData.testDataSource());
        Survey survey1 = new Survey();
        survey1.setName("Fiskeundersøkelse");
        surveyDao.saveSurvey(survey1);
    }

    @Test
    void shouldRetrieveSavedUsersFromDatabase() throws SQLException {
        User user = TestData.exampleUser();
        user.seteMail("fiskesluk@hotmail.no");
        userDao.saveUsers(user);
        userDao.setSurveyId(user.getSurveyDone(), user.getId());

        assertThat(userDao.retrieveUsers(user.getId()))
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    void shouldListSavedUsersByFirstname() throws SQLException, UnsupportedEncodingException {
        User matchingUser = TestData.exampleUser();
        matchingUser.setFirstName(TestData.exampleUser().getFirstName());
        matchingUser.setLastName(TestData.exampleUser().getLastName());
        userDao.saveUsers(matchingUser);

        User anotherMatchingUser = TestData.exampleUser();
        anotherMatchingUser.setFirstName(matchingUser.getFirstName());
        anotherMatchingUser.setLastName(matchingUser.getLastName());
        anotherMatchingUser.seteMail("hesterbest@yahoo.com");
        userDao.saveUsers(anotherMatchingUser);

        User nonMatchingUser = TestData.exampleUser();
        nonMatchingUser.setFirstName("dustemikkel");
        nonMatchingUser.setLastName("olsen");
        nonMatchingUser.seteMail("dustemikkel@hotmail.no");
        userDao.saveUsers(nonMatchingUser);

        System.out.println(matchingUser);
        System.out.println(anotherMatchingUser);

        assertThat(userDao.listByUsersName(matchingUser.getFirstName()))
                .extracting(User::getId)
                .contains(matchingUser.getId(), anotherMatchingUser.getId())
                .doesNotContain(nonMatchingUser.getId());
    }
}




















