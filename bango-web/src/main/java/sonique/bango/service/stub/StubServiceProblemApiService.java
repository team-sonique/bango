package sonique.bango.service.stub;

import sky.sns.spm.domain.model.DomainAgent;
import sky.sns.spm.domain.model.EventHistoryItem;
import sky.sns.spm.domain.model.diagnostic.sqc.SequenceOfAnswers;
import sky.sns.spm.domain.model.majorserviceproblem.DomainMajorServiceProblem;
import sky.sns.spm.domain.model.refdata.Cause;
import sky.sns.spm.domain.model.refdata.Fault;
import sky.sns.spm.domain.model.refdata.Queue;
import sky.sns.spm.domain.model.refdata.ResolutionReason;
import sky.sns.spm.domain.model.serviceproblem.*;
import sky.sns.spm.infrastructure.repository.DomainAgentRepository;
import sky.sns.spm.infrastructure.repository.DomainMajorServiceProblemRepository;
import sky.sns.spm.infrastructure.repository.DomainServiceProblemRepository;
import sky.sns.spm.infrastructure.repository.QueueRepository;
import sky.sns.spm.infrastructure.security.SpringSecurityAuthorisedActorProvider;
import sky.sns.spm.interfaces.shared.PagedSearchResults;
import sky.sns.spm.web.spmapp.shared.dto.SearchParametersDTO;
import sonique.bango.domain.sorter.Comparators;
import sonique.bango.service.ServiceProblemApiService;
import sonique.bango.util.PagedSearchResultsCreator;
import spm.domain.MajorServiceProblemId;
import spm.domain.QueueId;
import spm.domain.ServiceProblemId;
import spm.domain.event.ReassignHistoryEvent;
import spm.domain.event.TransferHistoryEvent;
import spm.domain.event.UnassignHistoryEvent;
import util.SupermanDataFixtures;

import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static sky.sns.spm.domain.model.serviceproblem.EventDescription.ServiceProblemTransferred;
import static sky.sns.spm.interfaces.shared.SystemActor.Spm;
import static sonique.datafixtures.PrimitiveDataFixtures.pickOneOf;

public class StubServiceProblemApiService implements ServiceProblemApiService {
    private final DomainServiceProblemRepository serviceProblemRepository;
    private final SpringSecurityAuthorisedActorProvider authorisedActorProvider;
    private final QueueRepository queueRepository;
    private final DomainAgentRepository agentRepository;
    private final DomainMajorServiceProblemRepository mspRepository;

    public StubServiceProblemApiService(
            DomainServiceProblemRepository serviceProblemRepository,
            SpringSecurityAuthorisedActorProvider authorisedActorProvider,
            QueueRepository queueRepository,
            DomainAgentRepository agentRepository,
            DomainMajorServiceProblemRepository mspRepository) {
        this.serviceProblemRepository = serviceProblemRepository;
        this.authorisedActorProvider = authorisedActorProvider;
        this.queueRepository = queueRepository;
        this.agentRepository = agentRepository;
        this.mspRepository = mspRepository;
    }

    @Override
    public DomainServiceProblem serviceProblemWithId(ServiceProblemId serviceProblemId) {
        return serviceProblemRepository.findByServiceProblemId(serviceProblemId);
    }

    @Override
    public EventHistoryItem addNote(ServiceProblemId serviceProblemId, String note) {
        DomainServiceProblem serviceProblem = serviceProblemWithId(serviceProblemId);
        DomainAgent agent = authorisedActorProvider.getLoggedInAgent();
        serviceProblem.addNote(agent, note);
        return ServiceProblemEventHistoryItem.createEvent(EventDescription.Note, new Date(), agent.getAgentCode(), note, serviceProblem);
    }

    @Override
    public PagedSearchResults<EventHistoryItem> eventHistory(ServiceProblemId serviceProblemId, SearchParametersDTO searchParameters) {
        List<EventHistoryItem> eventHistoryItems = serviceProblemWithId(serviceProblemId).historyItems();

        return PagedSearchResultsCreator.createPageFor(searchParameters, eventHistoryItems, new EventHistoryComparators());
    }

    @Override
    public DomainServiceProblem pull(ServiceProblemId serviceProblemId) {
        DomainServiceProblem serviceProblem = serviceProblemWithId(serviceProblemId);
        serviceProblem.tug(authorisedActorProvider.getLoggedInAgent());
        return serviceProblem;
    }

    @Override
    public DomainServiceProblem hold(ServiceProblemId serviceProblemId) {
        DomainServiceProblem serviceProblem = serviceProblemWithId(serviceProblemId);
        serviceProblem.holdWorkItem(authorisedActorProvider.getLoggedInAgent());

        return serviceProblem;
    }

    @Override
    public DomainServiceProblem release(ServiceProblemId serviceProblemId) {
        DomainServiceProblem serviceProblem = serviceProblemWithId(serviceProblemId);
        serviceProblem.unholdWorkItem(authorisedActorProvider.getLoggedInAgent());

        return serviceProblem;
    }

    @Override
    public DomainServiceProblem createWorkReminder(ServiceProblemId serviceProblemId, Date dateTime) {
        DomainServiceProblem serviceProblem = serviceProblemWithId(serviceProblemId);
        serviceProblem.createWorkReminder(
                authorisedActorProvider.getLoggedInAgent(),
                dateTime
        );

        return serviceProblem;
    }

    @Override
    public DomainServiceProblem transferToQueue(ServiceProblemId serviceProblemId, TransferType transferType, QueueId queueId) {
        Date date = new Date();
        DomainAgent loggedInAgent = authorisedActorProvider.getLoggedInAgent();
        DomainServiceProblem domainServiceProblem = serviceProblemWithId(serviceProblemId);
        Queue sourceQueue = domainServiceProblem.getQueue();
        Queue destinationQueue = queueRepository.getAllQueues().stream().filter(q -> q.id().equals(queueId)).findFirst().get();

        domainServiceProblem.transfer(destinationQueue);

        domainServiceProblem.writeHistoryFor(new TransferHistoryEvent(date, loggedInAgent, ServiceProblemTransferred, sourceQueue, destinationQueue));

        DomainWorkItem workItem = domainServiceProblem.workItem();
        workItem.setUnassigned();
        workItem.setAssignmentType(transferType.getAssignmentType());
        domainServiceProblem.writeHistoryFor(new UnassignHistoryEvent(date, Spm, loggedInAgent, ServiceProblemTransferred));
        return domainServiceProblem;
    }

    @Override
    public DomainServiceProblem clearServiceProblem(ServiceProblemId serviceProblemId, String fault, String cause, String resolution) {
        DomainServiceProblem serviceProblem = serviceProblemRepository.findByServiceProblemId(serviceProblemId);
        serviceProblem.clearByActor(
                new ServiceProblemResolution(new Fault(fault, fault), new Cause(cause, cause), new ResolutionReason(resolution, resolution)),
                authorisedActorProvider.getLoggedInAgent()
        );
        return serviceProblem;
    }

    @Override
    public DomainServiceProblem selectNextWorkItem(ServiceProblemId serviceProblemId, String nextWorkItem) {
        DomainServiceProblem serviceProblem = serviceProblemRepository.findByServiceProblemId(serviceProblemId);
        serviceProblem.selectNextWorkItem(WorkItemAction.valueOf(nextWorkItem), authorisedActorProvider.getLoggedInAgent());
        return serviceProblem;
    }

    @Override
    public DomainServiceProblem reassignToAgent(ServiceProblemId serviceProblemId, String agentCode) {
        DomainServiceProblem serviceProblem = serviceProblemRepository.findByServiceProblemId(serviceProblemId);
        DomainAgent targetAgent = agentRepository.findByAgentCode(agentCode);

        serviceProblem.workItem().assignTo(targetAgent);
        serviceProblem.writeHistoryFor(new ReassignHistoryEvent(new Date(), authorisedActorProvider.authorisedActor(), targetAgent));
        return serviceProblem;
    }

    @Override
    public PagedSearchResults<DomainServiceProblem> serviceProblems(SearchParametersDTO searchParameters) {
        return serviceProblemRepository.searchForServiceProblems(searchParameters);
    }

    @Override
    public DomainServiceProblem associateServiceProblemToMajorServiceProblem(ServiceProblemId serviceProblemId, MajorServiceProblemId majorServiceProblemId) {
        DomainServiceProblem serviceProblem = serviceProblemRepository.findByServiceProblemId(serviceProblemId);
        DomainMajorServiceProblem majorServiceProblem = mspRepository.findByMajorServiceProblemId(majorServiceProblemId);
        Queue newQueue = pickOneOf(queueRepository.getAllQueues().stream().filter(queue -> !queue.isDefaultWorkItemCreated()).collect(toList()));
        serviceProblem.associateToMajorServiceProblem(majorServiceProblem, newQueue, authorisedActorProvider.getLoggedInAgent());
        return serviceProblem;
    }

    @Override
    public DomainServiceProblem requestManagedLineTest(ServiceProblemId serviceProblemId, SequenceOfAnswers sequenceOfAnswers) {
        DomainServiceProblem serviceProblem = serviceProblemRepository.findByServiceProblemId(serviceProblemId);
        serviceProblem.managedLineTestRequested(authorisedActorProvider.getLoggedInAgent(), SupermanDataFixtures.someLineTestReference());
        return serviceProblem;
    }

    private static class EventHistoryComparators extends Comparators<EventHistoryItem> {
        public EventHistoryComparators() {
            add("eventType", (o1, o2) -> o1.type().compareTo(o2.type()));
            add("createdDate", (o1, o2) -> o1.createdDate().compareTo(o2.createdDate()));
            add("createdBy", (o1, o2) -> o1.createdBy().compareTo(o2.createdBy()));
        }
    }


}
