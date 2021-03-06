package sonique.bango.serviceproblem.eventhistorytoolbar;

import com.googlecode.yatspec.state.givenwhenthen.GivensBuilder;
import com.googlecode.yatspec.state.givenwhenthen.StateExtractor;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import sky.sns.spm.domain.model.serviceproblem.DomainServiceProblem;
import sky.sns.spm.domain.model.serviceproblem.EventDescription;
import sky.sns.spm.domain.model.serviceproblem.ServiceProblemEventHistoryItem;
import sonique.bango.BangoYatspecTest;
import sonique.bango.action.AddNoteAgentActions;
import sonique.bango.action.ViewServiceProblemAction;
import sonique.bango.driver.component.SupermanElement;
import sonique.bango.driver.component.form.SupermanButton;
import sonique.bango.driver.panel.dialog.AddNoteDialog;
import sonique.bango.driver.panel.serviceproblem.EventHistoryPanel;
import sonique.bango.matcher.EventHistoryItemMatcher;
import sonique.bango.matcher.IsDisplayed;
import sonique.bango.matcher.MockieMatcher;
import sonique.bango.matcher.panel.AbstractPanelMatcher;
import sonique.bango.scenario.ScenarioGivensBuilder;
import sonique.bango.scenario.ServiceProblemScenario;
import sonique.bango.service.ServiceProblemApiService;
import sonique.testsupport.matchers.AppendableAllOf;
import spm.domain.ServiceProblemId;

import java.util.Date;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static sonique.bango.matcher.ATitleOf.aTitleOf;
import static sonique.bango.matcher.IsDisabled.isDisabled;
import static sonique.bango.matcher.IsEnabled.isEnabled;
import static sonique.bango.matcher.IsNotDisplayed.isNotDisplayed;
import static sonique.bango.matcher.panel.EventHistoryPanelMatchers.eventHistoryItems;
import static util.SupermanDataFixtures.someNoteText;

public class AddNoteTest extends BangoYatspecTest {

    private DomainServiceProblem serviceProblem;
    private String theNote;
    private ServiceProblemEventHistoryItem expectedNote;

    @Before
    public void setUp() throws Exception {
        loginAgent();
        theNote = someNoteText();
        serviceProblem = ServiceProblemScenario.serviceProblemBuilder().build();
        expectedNote = ServiceProblemEventHistoryItem.createEvent(EventDescription.Note, new Date(), agentForTest.getAgentCode(), theNote, serviceProblem);
    }

    @Test
    public void addsHistoryItem() throws Exception {
        given(aServiceProblemIsOpen());
        and(theAgentIsViewingTheServiceProblem());

        when(theAgent().clicksTheAddNoteButton());
        then(theDialog(), isDisplayed().with(aTitleOf("Add Note")));

        when(theAgent().enterNote(theNote));

        then(theEventHistoryPanel(), showsTheReturnedNotes());
        and(theServiceProblemService(), wasCalledWith(theNote));
    }

    @Test
    public void canCancelTheAddHistoryItemDialog() throws Exception {
        given(aServiceProblemIsOpen());
        and(theAgentIsViewingTheServiceProblem());

        when(theAgent().clicksTheAddNoteButton());
        then(theDialog(), isDisplayed());

        when(theAgent().clicksCancelNoteButton());
        then(theDialog(), isNotDisplayed());
        and(theServiceProblemService(), isNotCalled());
    }

    @Test
    public void addNoteDialogButtonIsDisabledWhenTextEmpty() throws Exception {
        given(aServiceProblemIsOpen());
        and(theAgentIsViewingTheServiceProblem());

        when(theAgent().clicksTheAddNoteButton());
        then(theDialog(), isDisplayed().and(theAddNoteDialogButtonIsDisabled()));

        when(theAgent().typeSomeTextIntoTheNoteField());
        then(theDialog(), theAddNoteDialogButtonIsEnabled());

        when(theAgent().clearsTheNoteText());
        then(theDialog(), theAddNoteDialogButtonIsDisabled());

        and(theAgent().clicksCancelNoteButton());
    }

    private Matcher<? super AddNoteDialog> theAddNoteDialogButtonIsEnabled() {
        return theAddNoteDialogButton(isEnabled());
    }

    private Matcher<AddNoteDialog> theAddNoteDialogButtonIsDisabled() {
        return theAddNoteDialogButton(isDisabled());
    }

    private AbstractPanelMatcher<AddNoteDialog, SupermanButton> theAddNoteDialogButton(final AppendableAllOf<SupermanElement> matcher) {
        return new AbstractPanelMatcher<AddNoteDialog, SupermanButton>(matcher) {
            @Override
            protected SupermanButton actualValue(AddNoteDialog item) {
                return item.addNoteButton();
            }
        };
    }

    private Matcher<ServiceProblemApiService> isNotCalled() {
        return new MockieMatcher<ServiceProblemApiService>(never()) {
            @Override
            protected void doTheMock(ServiceProblemApiService serviceProblemApiService) {
                serviceProblemApiService.addNote(any(ServiceProblemId.class), any(String.class));
            }
        };
    }

    private Matcher<EventHistoryPanel> showsTheReturnedNotes() {
        return eventHistoryItems(hasItem(new EventHistoryItemMatcher(expectedNote)));
    }

    private StateExtractor<EventHistoryPanel> theEventHistoryPanel() {
        return inputAndOutputs -> supermanApp.appContainer().tab().serviceProblem(serviceProblem).tabContent().eventHistoryPanel();
    }

    private AddNoteAgentActions theAgent() {
        return new AddNoteAgentActions(supermanApp, serviceProblem);
    }

    private AppendableAllOf<AddNoteDialog> isDisplayed() {
        return IsDisplayed.isDisplayed();
    }

    private StateExtractor<AddNoteDialog> theDialog() {
        return inputAndOutputs -> supermanApp.dialogs().addNote();
    }

    private Matcher<ServiceProblemApiService> wasCalledWith(final String theNote) {
        return new MockieMatcher<ServiceProblemApiService>() {
            @Override
            protected void doTheMock(ServiceProblemApiService serviceProblemApiService) {
                serviceProblemApiService.addNote(serviceProblem.serviceProblemId(), theNote);
            }
        };
    }

    private StateExtractor<ServiceProblemApiService> theServiceProblemService() {
        return inputAndOutputs -> scenarioDriver().servicesFor(agentForTest).serviceProblemApiService();
    }

    private GivensBuilder theAgentIsViewingTheServiceProblem() {
        return givens -> {
            new ViewServiceProblemAction(supermanApp, serviceProblem).goBango();
            return givens;
        };
    }

    private GivensBuilder aServiceProblemIsOpen() {
        ServiceProblemScenario supermanScenario = new ServiceProblemScenario(scenarioDriver(), agentForTest, serviceProblem)
                .returnsWhenNoteAdded(expectedNote);
        return new ScenarioGivensBuilder(supermanScenario);
    }
}