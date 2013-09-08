package sonique.bango.matcher;

import com.google.common.base.Predicate;
import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.FluentWait;

import java.util.concurrent.TimeUnit;

public abstract class AsynchronousMatcher<T> extends TypeSafeMatcher<T> {
    @Override
    public boolean matchesSafely(T item) {
        try {
            new FluentWait<T>(item)
                    .withTimeout(5, TimeUnit.SECONDS)
                    .pollingEvery(100, TimeUnit.MILLISECONDS)
                    .until(until());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected abstract Predicate<T> until();

    @Override
    public void describeTo(Description description) {
        throw new UnsupportedOperationException("method describeTo not yet implemented ");
    }
}
