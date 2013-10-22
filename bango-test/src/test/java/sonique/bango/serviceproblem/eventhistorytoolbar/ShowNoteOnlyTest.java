package sonique.bango.serviceproblem.eventhistorytoolbar;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.googlecode.yatspec.state.givenwhenthen.*;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import sky.sns.spm.domain.model.EventHistoryItem;
import sky.sns.spm.domain.model.serviceproblem.DomainServiceProblem;
import sky.sns.spm.domain.model.serviceproblem.EventDescription;
import sonique.bango.BangoYatspecTest;
import sonique.bango.action.ViewServiceProblemAction;
import sonique.bango.driver.panel.serviceproblem.EventHistoryPanel;
import sonique.bango.scenario.ScenarioGivensBuilder;
import sonique.bango.scenario.ServiceProblemScenario;

import static com.google.common.collect.Lists.newArrayList;
import static sonique.bango.matcher.EventHistoryMatcher.eventHistoryMatches;
import static sonique.bango.matcher.panel.EventHistoryPanelMatchers.eventHistoryItems;
import static sonique.bango.util.BangoDataFixtures.someEventHistoryItemsFor;

public class ShowNoteOnlyTest extends BangoYatspecTest {

    private DomainServiceProblem serviceProblem;

    @Before
    public void setup() {
        loginAgent();
    }

    @Test
    public void canShowNotesOnly() throws Exception {
        given(aServiceProblemIsOpenWithNotes());
        and(theAgentIsViewingTheServiceProblem());

        when(theAgentClicksTheShowNotesOnlyButton());
        then(theEventHistoryPanel(), showsOnlyNotes());

        when(theAgentClicksTheShowNotesOnlyButton());
        then(theEventHistoryPanel(), showsAllEventHistory());
    }

    private Matcher<? super EventHistoryPanel> showsAllEventHistory() {
        return eventHistoryItems(eventHistoryMatches(serviceProblem.historyItems()));
    }

    private Matcher<EventHistoryPanel> showsOnlyNotes() {
        Iterable<EventHistoryItem> onlyNotes = Iterables.filter(serviceProblem.historyItems(), new Predicate<EventHistoryItem>() {
            @Override
            public boolean apply(EventHistoryItem input) {
                return input.type() == EventDescription.Note;
            }
        });

        return eventHistoryItems(eventHistoryMatches(newArrayList(onlyNotes)));
    }

    private StateExtractor<EventHistoryPanel> theEventHistoryPanel() {
        return new StateExtractor<EventHistoryPanel>() {
            @Override
            public EventHistoryPanel execute(CapturedInputAndOutputs inputAndOutputs) throws Exception {
                return supermanApp.appContainer().serviceProblemTab(serviceProblem.serviceProblemId()).tabContent().eventHistoryPanel();
            }
        };
    }

    private ActionUnderTest theAgentClicksTheShowNotesOnlyButton() {
        return new ActionUnderTest() {
            @Override
            public CapturedInputAndOutputs execute(InterestingGivens givens, CapturedInputAndOutputs capturedInputAndOutputs) throws Exception {
                supermanApp.appContainer().serviceProblemTab(serviceProblem.serviceProblemId()).tabContent().eventHistoryPanel().eventHistoryToolbar().showNotesOnlyButton().click();

                return capturedInputAndOutputs;
            }
        };
    }

    private GivensBuilder theAgentIsViewingTheServiceProblem() {
        return new GivensBuilder() {
            @Override
            public InterestingGivens build(InterestingGivens givens) throws Exception {
                new ViewServiceProblemAction(supermanApp, serviceProblem).goBango();
                return givens;
            }
        };
    }

    private GivensBuilder aServiceProblemIsOpenWithNotes() {
        serviceProblem = ServiceProblemScenario.serviceProblemBuilder().build();

        serviceProblem.historyItems().addAll(someEventHistoryItemsFor(serviceProblem, EventDescription.Note));
        ServiceProblemScenario supermanScenario = new ServiceProblemScenario(scenarioDriver(), agentForTest, serviceProblem);

        return new ScenarioGivensBuilder(supermanScenario);
    }
}
