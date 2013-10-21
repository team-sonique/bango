package sonique.bango.serviceproblem;

import com.googlecode.yatspec.state.givenwhenthen.*;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import sky.sns.spm.domain.model.serviceproblem.DomainServiceProblem;
import sky.sns.spm.domain.model.serviceproblem.ServiceProblemEventHistoryItem;
import sonique.bango.BangoYatspecTest;
import sonique.bango.driver.panel.serviceproblem.EventHistoryPanel;
import sonique.bango.matcher.IsDisplayed;
import sonique.bango.scenario.ServiceProblemScenario;
import sonique.bango.service.ServiceProblemApiService;
import sonique.testsupport.matchers.AppendableAllOf;

import static sonique.bango.matcher.ATitleOf.aTitleOf;
import static sonique.bango.matcher.EventHistoryMatcher.eventHistoryMatches;
import static sonique.bango.matcher.panel.EventHistoryPanelMatchers.eventHistoryItems;
import static sonique.datafixtures.DateTimeDataFixtures.someDateInTheNextYear;
import static sonique.datafixtures.PrimitiveDataFixtures.someNumberBetween;
import static sonique.testsupport.matchers.AppendableAllOf.thatHas;
import static util.SupermanDataFixtures.*;

public class EventHistoryPanelTest extends BangoYatspecTest {

    private DomainServiceProblem serviceProblem;
    private String theNote;

    @Before
    public void setUp() throws Exception {
        loginAgent();
        theNote = "the Note";
    }

    @Test
    public void displaysEventHistory() throws Exception {
        given(aServiceProblemWithSomeHistoryEvents());

        when(aServiceProblemIsDisplayed());

        then(theEventHistoryPanel(), isDisplayed()
                .with(theEventHistoryItems())
        );
    }

    @Ignore
    @Test
    public void addsHistoryItem() throws Exception {
        given(aServiceProblemIsOpenAndDisplayed());

        when(theAgentAddsANewNote());

        then(theServiceProblemService(), isCalledWith(theNote));
//        and(theResults(), areDisplayed());
    }

    private Matcher<ServiceProblemApiService> isCalledWith(final String theNote) {
        return new TypeSafeMatcher<ServiceProblemApiService>() {
            @Override
            protected boolean matchesSafely(ServiceProblemApiService item) {
                Mockito.verify(item).addNote(serviceProblem.serviceProblemId(), theNote);
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("wasn't called");
            }
        };
    }

    private StateExtractor<ServiceProblemApiService> theServiceProblemService() {
        return new StateExtractor<ServiceProblemApiService>() {
            @Override
            public ServiceProblemApiService execute(CapturedInputAndOutputs inputAndOutputs) throws Exception {
                return scenarioDriver().servicesFor(agentForTest).serviceProblemApiService();
            }
        };
    }

    private GivensBuilder aServiceProblemWithSomeHistoryEvents() {
        serviceProblem = ServiceProblemScenario.serviceProblemBuilder().build();
        for (int i = 0; i < someNumberBetween(3, 7); i++) {
            serviceProblem.historyItems().add(ServiceProblemEventHistoryItem.createEvent(someEventDescription(), someDateInTheNextYear().toDate(), someAgent().getActorName(), someNoteText(), serviceProblem));
        }

        return scenarioGivensBuilderFor(serviceProblem);
    }

    private GivensBuilder aServiceProblemIsOpenAndDisplayed() {
        serviceProblem = ServiceProblemScenario.serviceProblemBuilder().build();
        supermanApp.appContainer().searchPanel().searchFor(serviceProblem.serviceProblemId());

        return scenarioGivensBuilderFor(serviceProblem);
    }

    private ActionUnderTest aServiceProblemIsDisplayed() {
        return new ActionUnderTest() {
            @Override
            public CapturedInputAndOutputs execute(InterestingGivens givens, CapturedInputAndOutputs capturedInputAndOutputs) throws Exception {
                supermanApp.appContainer().searchPanel().searchFor(serviceProblem.serviceProblemId());

                return capturedInputAndOutputs;
            }
        };
    }

    private ActionUnderTest theAgentAddsANewNote() {
        return new ActionUnderTest() {
            @Override
            public CapturedInputAndOutputs execute(InterestingGivens givens, CapturedInputAndOutputs capturedInputAndOutputs) throws Exception {
//                supermanApp.appContainer(). adds theNote
                return capturedInputAndOutputs;
            }
        };
    }

    private StateExtractor<EventHistoryPanel> theEventHistoryPanel() {
        return new StateExtractor<EventHistoryPanel>() {
            @Override
            public EventHistoryPanel execute(CapturedInputAndOutputs inputAndOutputs) throws Exception {
                return supermanApp.appContainer().serviceProblemTab(serviceProblem.serviceProblemId()).tabContent().eventHistoryPanel();
            }
        };
    }

    private StateExtractor<EventHistoryPanel> theNote() {
        return null;
    }

    private Matcher<EventHistoryPanel> theEventHistoryItems() {
        return eventHistoryItems(eventHistoryMatches(serviceProblem.historyItems()));
    }

    private Matcher<EventHistoryPanel> theCorrectDetails() {
        return null;
    }

    private AppendableAllOf<EventHistoryPanel> isDisplayed() {
        return thatHas(IsDisplayed.<EventHistoryPanel>isDisplayed()).with(aTitleOf("Event History"));
    }
}
