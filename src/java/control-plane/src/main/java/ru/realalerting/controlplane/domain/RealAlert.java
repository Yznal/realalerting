package ru.realalerting.controlplane.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.realalerting.controlplane.domain.enumeration.AlertType;

/**
 * A RealAlert.
 */
@Entity
@Table(name = "real_alert")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RealAlert implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AlertType type;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "conf")
    private String conf;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "realAlert")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "client", "realAlert" }, allowSetters = true)
    private Set<AlertSubscriber> alertSubscribers = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "metrics", "metricSubscribers", "realAlerts", "alertSubscribers", "tenant" }, allowSetters = true)
    private Client client;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "realAlerts", "metricSubscribers", "client", "metricTagsValue" }, allowSetters = true)
    private Metric metric;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Integer getId() {
        return this.id;
    }

    public RealAlert id(Integer id) {
        this.setId(id);
        return this;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AlertType getType() {
        return this.type;
    }

    public RealAlert type(AlertType type) {
        this.setType(type);
        return this;
    }

    public void setType(AlertType type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public RealAlert name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public RealAlert description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConf() {
        return this.conf;
    }

    public RealAlert conf(String conf) {
        this.setConf(conf);
        return this;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

    public Set<AlertSubscriber> getAlertSubscribers() {
        return this.alertSubscribers;
    }

    public void setAlertSubscribers(Set<AlertSubscriber> alertSubscribers) {
        if (this.alertSubscribers != null) {
            this.alertSubscribers.forEach(i -> i.setRealAlert(null));
        }
        if (alertSubscribers != null) {
            alertSubscribers.forEach(i -> i.setRealAlert(this));
        }
        this.alertSubscribers = alertSubscribers;
    }

    public RealAlert alertSubscribers(Set<AlertSubscriber> alertSubscribers) {
        this.setAlertSubscribers(alertSubscribers);
        return this;
    }

    public RealAlert addAlertSubscriber(AlertSubscriber alertSubscriber) {
        this.alertSubscribers.add(alertSubscriber);
        alertSubscriber.setRealAlert(this);
        return this;
    }

    public RealAlert removeAlertSubscriber(AlertSubscriber alertSubscriber) {
        this.alertSubscribers.remove(alertSubscriber);
        alertSubscriber.setRealAlert(null);
        return this;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public RealAlert client(Client client) {
        this.setClient(client);
        return this;
    }

    public Metric getMetric() {
        return this.metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public RealAlert metric(Metric metric) {
        this.setMetric(metric);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RealAlert)) {
            return false;
        }
        return getId() != null && getId().equals(((RealAlert) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RealAlert{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", conf='" + getConf() + "'" +
            "}";
    }
}
