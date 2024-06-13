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

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MetricType type;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "critical_alert_producer_address")
    private String criticalAlertProducerAddress;

    @Column(name = "critical_alert_producer_port")
    private Integer criticalAlertProducerPort;

    @Column(name = "critical_alert_producer_uri")
    private String criticalAlertProducerUri;

    @Column(name = "critical_alert_producer_stream_id")
    private Integer criticalAlertProducerStreamId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "metric")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "alertSubscribers", "client", "metric" }, allowSetters = true)
    private Set<RealAlert> realAlerts = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "metric")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "client", "metric" }, allowSetters = true)
    private Set<MetricSubscriber> metricSubscribers = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "metrics", "metricSubscribers", "realAlerts", "alertSubscribers", "tenant" }, allowSetters = true)
    private Client client;

    @JsonIgnoreProperties(value = { "metric", "tenant" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "metric")
    private MetricTagsValue metricTagsValue;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Integer getId() {
        return this.id;
    }

    public Metric id(Integer id) {
        this.setId(id);
        return this;
    }

    public void setId(Integer id) {
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

    public String getCriticalAlertProducerAddress() {
        return this.criticalAlertProducerAddress;
    }

    public Metric criticalAlertProducerAddress(String criticalAlertProducerAddress) {
        this.setCriticalAlertProducerAddress(criticalAlertProducerAddress);
        return this;
    }

    public void setCriticalAlertProducerAddress(String criticalAlertProducerAddress) {
        this.criticalAlertProducerAddress = criticalAlertProducerAddress;
    }

    public Integer getCriticalAlertProducerPort() {
        return this.criticalAlertProducerPort;
    }

    public Metric criticalAlertProducerPort(Integer criticalAlertProducerPort) {
        this.setCriticalAlertProducerPort(criticalAlertProducerPort);
        return this;
    }

    public void setCriticalAlertProducerPort(Integer criticalAlertProducerPort) {
        this.criticalAlertProducerPort = criticalAlertProducerPort;
    }

    public String getCriticalAlertProducerUri() {
        return this.criticalAlertProducerUri;
    }

    public Metric criticalAlertProducerUri(String criticalAlertProducerUri) {
        this.setCriticalAlertProducerUri(criticalAlertProducerUri);
        return this;
    }

    public void setCriticalAlertProducerUri(String criticalAlertProducerUri) {
        this.criticalAlertProducerUri = criticalAlertProducerUri;
    }

    public Integer getCriticalAlertProducerStreamId() {
        return this.criticalAlertProducerStreamId;
    }

    public Metric criticalAlertProducerStreamId(Integer criticalAlertProducerStreamId) {
        this.setCriticalAlertProducerStreamId(criticalAlertProducerStreamId);
        return this;
    }

    public void setCriticalAlertProducerStreamId(Integer criticalAlertProducerStreamId) {
        this.criticalAlertProducerStreamId = criticalAlertProducerStreamId;
    }

    public Set<RealAlert> getRealAlerts() {
        return this.realAlerts;
    }

    public void setRealAlerts(Set<RealAlert> realAlerts) {
        if (this.realAlerts != null) {
            this.realAlerts.forEach(i -> i.setMetric(null));
        }
        if (realAlerts != null) {
            realAlerts.forEach(i -> i.setMetric(this));
        }
        this.realAlerts = realAlerts;
    }

    public Metric realAlerts(Set<RealAlert> realAlerts) {
        this.setRealAlerts(realAlerts);
        return this;
    }

    public Metric addRealAlert(RealAlert realAlert) {
        this.realAlerts.add(realAlert);
        realAlert.setMetric(this);
        return this;
    }

    public Metric removeRealAlert(RealAlert realAlert) {
        this.realAlerts.remove(realAlert);
        realAlert.setMetric(null);
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
            ", criticalAlertProducerAddress='" + getCriticalAlertProducerAddress() + "'" +
            ", criticalAlertProducerPort=" + getCriticalAlertProducerPort() +
            ", criticalAlertProducerUri='" + getCriticalAlertProducerUri() + "'" +
            ", criticalAlertProducerStreamId=" + getCriticalAlertProducerStreamId() +
            "}";
    }
}
