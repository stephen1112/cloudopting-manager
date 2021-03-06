package eu.cloudopting.domain;

import eu.cloudopting.events.api.entity.BaseEntity;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Set;

import eu.cloudopting.jsonserializer.ApplicationSummarySerializer;

@Configurable
@Entity
@Table(schema = "public",name = "customizations")

public class Customizations implements BaseEntity {

	@ManyToOne
    @JoinColumn(name = "customer_organization_id", referencedColumnName = "id")
    private Organizations customerOrganizationId;

    @ManyToOne
    @JoinColumn(name = "cloud_account_id", referencedColumnName = "id")
    private CloudAccounts cloudAccount;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "application_id", referencedColumnName = "id")
    @JsonSerialize(using = ApplicationSummarySerializer.class)
    private Applications applicationId;

	@Column(name = "customization_tosca_file")
    @NotNull
    private String customizationToscaFile;

	@Column(name = "customization_creation")
    @NotNull
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "M-")
    private Date customizationCreation;

	@Column(name = "customization_activation")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "M-")
    private Date customizationActivation;

	@Column(name = "customization_decommission")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "M-")
    private Date customizationDecommission;

	@ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private CustomizationStatus statusId;
	
	@Column(name = "process_id", length = 64)
    private String processId;
	
	//See: http://stackoverflow.com/questions/28588311/correct-jpa-annotation-for-postgresqls-text-type-without-hibernate-annotations
	//and  http://stackoverflow.com/questions/3868096/jpa-how-do-i-persist-a-string-into-a-database-field-type-mysql-text
	//Executed the following SQL Command on the db: ALTER TABLE customizations ADD COLUMN customization_form_value text;
//	@Lob
	@Column(name = "customization_form_value")
    private String customizationFormValue;

    @Column(name = "pay_service")
    private Boolean payService;
    
    @Column(name = "pay_platform")
    private Boolean payPlatform;
    
    @Column(name = "is_trial")
    private Boolean isTrial;
    
	@Column(name = "trial_end_date")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "M-")
    private Date trialEndDate;
	
	@OneToMany(mappedBy = "customization")
    private Set<CustomizationZabbixMonitor> zabbixMonitors;
	
	@OneToMany(mappedBy = "customization")
    private Set<CustomizationDeployInfo> deployInfos;
	
	@OneToMany(mappedBy = "customization")
    private Set<MonitoringInfoElastic> elasticInfos;
	
	public Organizations getCustomerOrganizationId() {
        return customerOrganizationId;
    }

	public void setCustomerOrganizationId(Organizations customerOrganizationId) {
        this.customerOrganizationId = customerOrganizationId;
    }

	public Applications getApplicationId() {
        return applicationId;
    }

	public void setApplicationId(Applications applicationId) {
        this.applicationId = applicationId;
    }

	public String getCustomizationToscaFile() {
        return customizationToscaFile;
    }

	public void setCustomizationToscaFile(String customizationToscaFile) {
        this.customizationToscaFile = customizationToscaFile;
    }

	public Date getCustomizationCreation() {
        return customizationCreation;
    }

	public void setCustomizationCreation(Date customizationCreation) {
        this.customizationCreation = customizationCreation;
    }

	public Date getCustomizationActivation() {
        return customizationActivation;
    }

	public void setCustomizationActivation(Date customizationActivation) {
        this.customizationActivation = customizationActivation;
    }

	public Date getCustomizationDecommission() {
        return customizationDecommission;
    }

	public void setCustomizationDecommission(Date customizationDecommission) {
        this.customizationDecommission = customizationDecommission;
    }

	public CustomizationStatus getStatusId() {
        return statusId;
    }

	public void setStatusId(CustomizationStatus statusId) {
        this.statusId = statusId;
    }

	public String getProcessId() {
        return processId;
    }

	public void setProcessId(String processId) {
        this.processId = processId;
    }

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	public Long getId() {
        return this.id;
    }

	public void setId(Long id) {
        this.id = id;
    }

	@PersistenceContext
    transient EntityManager entityManager;

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("");

	public static final EntityManager entityManager() {
        EntityManager em = new Customizations().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long countCustomizationses() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Customizations o", Long.class).getSingleResult();
    }

	public static List<Customizations> findAllCustomizationses() {
        return entityManager().createQuery("SELECT o FROM Customizations o", Customizations.class).getResultList();
    }

	public static List<Customizations> findAllCustomizationses(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Customizations o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Customizations.class).getResultList();
    }

	public static Customizations findCustomizations(Long id) {
        if (id == null) return null;
        return entityManager().find(Customizations.class, id);
    }

	public static List<Customizations> findCustomizationsEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Customizations o", Customizations.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	public static List<Customizations> findCustomizationsEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Customizations o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Customizations.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

	@Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Customizations attached = Customizations.findCustomizations(this.id);
            this.entityManager.remove(attached);
        }
    }

	@Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

	@Transactional
    public void clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }

	@Transactional
    public Customizations merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Customizations merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public CloudAccounts getCloudAccount() {
        return cloudAccount;
    }

    public void setCloudAccount(CloudAccounts cloudAccount) {
        this.cloudAccount = cloudAccount;
    }

	public String getCustomizationFormValue() {
		return customizationFormValue;
	}

	public void setCustomizationFormValue(String customizationFormValue) {
		this.customizationFormValue = customizationFormValue;
	}

	public Boolean getPayService() {
		return payService;
	}

	public void setPayService(Boolean payService) {
		this.payService = payService;
	}

	public Boolean getPayPlatform() {
		return payPlatform;
	}

	public void setPayPlatform(Boolean payPlatform) {
		this.payPlatform = payPlatform;
	}

	public Boolean getIsTrial() {
		return isTrial;
	}

	public void setIsTrial(Boolean isTrial) {
		this.isTrial = isTrial;
	}

	public Date getTrialEndDate() {
		return trialEndDate;
	}

	public void setTrialEndDate(Date trialEndDate) {
		this.trialEndDate = trialEndDate;
	}

	public Set<CustomizationZabbixMonitor> getZabbixMonitors() {
		return zabbixMonitors;
	}

	public void setZabbixMonitors(Set<CustomizationZabbixMonitor> zabbixMonitors) {
		this.zabbixMonitors = zabbixMonitors;
	}

	public Set<CustomizationDeployInfo> getDeployInfos() {
		return deployInfos;
	}

	public void setDeployInfos(Set<CustomizationDeployInfo> deployInfos) {
		this.deployInfos = deployInfos;
	}

	public Set<MonitoringInfoElastic> getElasticInfos() {
		return elasticInfos;
	}

	public void setElasticInfos(Set<MonitoringInfoElastic> elasticInfos) {
		this.elasticInfos = elasticInfos;
	}
}
