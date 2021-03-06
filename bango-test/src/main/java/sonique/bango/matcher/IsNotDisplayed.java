package sonique.bango.matcher;

import com.google.common.base.Predicate;
import sonique.bango.driver.component.SupermanElement;
import sonique.bango.driver.predicate.IsDisplayedPredicate;
import sonique.testsupport.matchers.AppendableAllOf;

import static com.google.common.base.Predicates.not;

public class IsNotDisplayed<T extends SupermanElement> extends AsynchronousMatcher<T> {

    public static <T extends SupermanElement> AppendableAllOf<T> isNotDisplayed() {
        return AppendableAllOf.thatHas(new IsNotDisplayed<T>());
    }

    private IsNotDisplayed() {
    }

    @Override
    protected Predicate<T> until() {
        return not(IsDisplayedPredicate.<T>isDisplayed());
    }

    @Override
    protected String expectedDescription() {
        return "element not to be displayed";
    }

    @Override
    protected String actualDescription(T actual) {
        return "was displayed";
    }
}
