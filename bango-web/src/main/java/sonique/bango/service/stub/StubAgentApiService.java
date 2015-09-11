package sonique.bango.service.stub;

import sky.sns.spm.domain.model.AgentAvailability;
import sky.sns.spm.domain.model.DomainAgent;
import sky.sns.spm.domain.model.DomainTeam;
import sky.sns.spm.domain.model.serviceproblem.DomainServiceProblem;
import sky.sns.spm.infrastructure.repository.DomainAgentRepository;
import sky.sns.spm.infrastructure.repository.DomainServiceProblemRepository;
import sky.sns.spm.infrastructure.repository.DomainTeamRepository;
import sky.sns.spm.infrastructure.security.SpringSecurityAuthorisedActorProvider;
import sky.sns.spm.interfaces.shared.PagedSearchResults;
import sky.sns.spm.web.spmapp.shared.dto.AgentStateDTO;
import sky.sns.spm.web.spmapp.shared.dto.SearchParametersDTO;
import sonique.bango.service.AgentApiService;
import spm.domain.TeamId;

import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class StubAgentApiService implements AgentApiService {

    private final SpringSecurityAuthorisedActorProvider authorisedActorProvider;
    private final DomainServiceProblemRepository serviceProblemRepository;
    private final DomainAgentRepository agentRepository;
    private final DomainTeamRepository teamRepository;

    public StubAgentApiService(
            SpringSecurityAuthorisedActorProvider authorisedActorProvider,
            DomainServiceProblemRepository serviceProblemRepository,
            DomainAgentRepository agentRepository,
            DomainTeamRepository teamRepository) {
        this.authorisedActorProvider = authorisedActorProvider;
        this.serviceProblemRepository = serviceProblemRepository;
        this.agentRepository = agentRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    public DomainAgent authenticatedAgent() {
        return authorisedActorProvider.getLoggedInAgent();
    }

    @Override
    public AgentStateDTO toggleAvailability() {
        AgentAvailability availability = authenticatedAgent().availability();
        if (availability == AgentAvailability.Available) {
            this.authenticatedAgent().makeAvailable(false, new Date());
        } else if (availability == AgentAvailability.Unavailable) {
            this.authenticatedAgent().makeAvailable(true, new Date());
        }

        return agentState();
    }

    @Override
    public PagedSearchResults<DomainServiceProblem> myItems(SearchParametersDTO searchParameters) {
        List<DomainServiceProblem> serviceProblemsForAgent = serviceProblemRepository.getServiceProblemsForAgent(authorisedActorProvider.getLoggedInAgent());

        List<DomainServiceProblem> pageOfServiceProblems = serviceProblemsForAgent.stream()
                .skip(searchParameters.getStartRow())
                .limit(searchParameters.getPageSize())
                .collect(toList());

        return new PagedSearchResults<>(pageOfServiceProblems, (long) serviceProblemsForAgent.size());
    }

    @Override
    public PagedSearchResults<DomainAgent> allAgents(Integer start, Integer limit) {
        List<DomainAgent> allAgents = agentRepository.getAllAgents();

        List<DomainAgent> pageOfAgents = allAgents.stream().skip(start).limit(limit).collect(toList());
        return new PagedSearchResults<>(pageOfAgents, (long) allAgents.size());
    }

    @Override
    public DomainAgent reassignAgent(String agentCode, String currentTeam, String newTeam) {
        DomainAgent domainAgent = agentRepository.findByAgentCode(agentCode);
        DomainTeam team = teamRepository.getTeam(new TeamId(newTeam));
        domainAgent.reassignTeam(team);
        return domainAgent;
    }

    @Override
    public AgentStateDTO agentState() {
        return statisticsFor(myItems(SearchParametersDTO.withNoSearchProperties(Integer.MAX_VALUE, 0)).getData());
    }

    private AgentStateDTO statisticsFor(List<DomainServiceProblem> serviceProblemsForAgent) {
        int pullCount = 0;
        int pushCount = 0;
        int heldCount = 0;

        for (DomainServiceProblem domainServiceProblem : serviceProblemsForAgent) {
            if (domainServiceProblem.hasWorkItem()) {
                switch (domainServiceProblem.workItem().assignmentType()) {
                    case Pull:
                        pullCount++;
                        break;
                    case Push:
                        pushCount++;
                        break;
                }
                if (domainServiceProblem.workItem().isHeld()) {
                    heldCount++;
                }
            }
        }
        return new AgentStateDTO(authenticatedAgent().availability(), "", serviceProblemsForAgent.size() - heldCount, heldCount, pullCount, pushCount);
    }
}