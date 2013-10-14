package sonique.bango.driver.panel;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import sonique.bango.driver.AppContainer;
import sonique.bango.driver.component.SupermanComponent;
import spm.domain.ServiceProblemId;

import static org.openqa.selenium.By.id;

public class ServiceProblemTab extends SupermanComponent implements HasTitle {

    private final ServiceProblemId serviceProblemId;

    public ServiceProblemTab(AppContainer appContainer, ServiceProblemId serviceProblemId) {
        super(appContainer, id(String.format("service-problem-tab-%d", serviceProblemId.asLong())));
        this.serviceProblemId = serviceProblemId;
    }

    @Override
    public String title() {
        WebElement titleElement = element().findElement(By.cssSelector("span.x-tab-inner"));

        return titleElement.getText();
    }

    public ServiceProblemTabContent tabContent() {
        return new ServiceProblemTabContent(parentElement, serviceProblemId);
    }
}
