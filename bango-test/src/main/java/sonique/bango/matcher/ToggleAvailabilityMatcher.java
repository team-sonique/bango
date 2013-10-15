package sonique.bango.matcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import sonique.bango.driver.panel.navigation.AgentStatusPanel;

public class ToggleAvailabilityMatcher extends TypeSafeMatcher<AgentStatusPanel> {
    private final Matcher matcher;

    public static ToggleAvailabilityMatcher theAvailabilityButton(Matcher matcher) {
        return new ToggleAvailabilityMatcher(matcher);
    }

    public ToggleAvailabilityMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    @Override
    protected boolean matchesSafely(AgentStatusPanel item) {
        return matcher.matches(item.availabilityButton());
    }

    @Override
    public void describeTo(Description description) {
        description.appendDescriptionOf(matcher);
    }
}