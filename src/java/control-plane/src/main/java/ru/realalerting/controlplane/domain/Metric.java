package ru.realalerting.controlplane.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.realalerting.controlplane.domain.enumeration.MetricType;

/**
 * A Metric.
 */
@Entity
@Table(name = "metric")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Metric implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MetricType type;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "metric")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "metric" }, allowSetters = true)
    private Set<Alert> alerts = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "metric")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "client", "metric" }, allowSetters = true)
    private Set<MetricSubscriber> metricSubscribers = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "metrics", "metricSubscribers", "tenant" }, allowSetters = true)
    private Client client;

    @JsonIgnoreProperties(value = { "metric", "tenant" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "metric")
    private MetricTagsValue metricTagsValue;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Metric id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MetricType getType() {
        return this.type;
    }

    public Metric type(MetricType type) {
        this.setType(type);
        return this;
    }

    public void setType(MetricType type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public Metric name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Metric description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Alert> getAlerts() {
        return this.alerts;
    }

    public void setAlerts(Set<Alert> alerts) {
        if (this.alerts != null) {
            this.alerts.forEach(i -> i.setMetric(null));
        }
        if (alerts != null) {
            alerts.forEach(i -> i.setMetric(this));
        }
        this.alerts = alerts;
    }

    public Metric alerts(Set<Alert> alerts) {
        this.setAlerts(alerts);
        return this;
    }

    public Metric addAlert(Alert alert) {
        this.alerts.add(alert);
        alert.setMetric(this);
        return this;
    }

    public Metric removeAlert(Alert alert) {
        this.alerts.remove(alert);
        alert.setMetric(null);
        return this;
    }

    public Set<MetricSubscriber> getMetricSubscribers() {
        return this.metricSubscribers;
    }

    public void setMetricSubscribers(Set<MetricSubscriber> metricSubscribers) {
        if (this.metricSubscribers != null) {
            this.metricSubscribers.forEach(i -> i.setMetric(null));
        }
        if (metricSubscribers != null) {
            metricSubscribers.forEach(i -> i.setMetric(this));
        }
        this.metricSubscribers = metricSubscribers;
    }

    public Metric metricSubscribers(Set<MetricSubscriber> metricSubscribers) {
        this.setMetricSubscribers(metricSubscribers);
        return this;
    }

    public Metric addMetricSubscriber(MetricSubscriber metricSubscriber) {
        this.metricSubscribers.add(metricSubscriber);
        metricSubscriber.setMetric(this);
        return this;
    }

    public Metric removeMetricSubscriber(MetricSubscriber metricSubscriber) {
        this.metricSubscribers.remove(metricSubscriber);
        metricSubscriber.setMetric(null);
        return this;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Metric client(Client client) {
        this.setClient(client);
        return this;
    }

    public MetricTagsValue getMetricTagsValue() {
        return this.metricTagsValue;
    }

    public void setMetricTagsValue(MetricTagsValue metricTagsValue) {
        if (this.metricTagsValue != null) {
            this.metricTagsValue.setMetric(null);
        }
        if (metricTagsValue != null) {
            metricTagsValue.setMetric(this);
        }
        this.metricTagsValue = metricTagsValue;
    }

    public Metric metricTagsValue(MetricTagsValue metricTagsValue) {
        this.setMetricTagsValue(metricTagsValue);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Metric)) {
            return false;
        }
        return getId() != null && getId().equals(((Metric) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Metric{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
