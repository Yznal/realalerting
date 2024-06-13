package ru.realalerting.controlplane.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Client.
 */
@Entity
@Table(name = "client")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Client implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "protocol_producer_address")
    private String protocolProducerAddress;

    @Column(name = "protocol_producer_port")
    private Integer protocolProducerPort;

    @Column(name = "protocol_producer_uri")
    private String protocolProducerUri;

    @Column(name = "protocol_producer_stream_id")
    private Integer protocolProducerStreamId;

    @Column(name = "protocol_subscriber_address")
    private String protocolSubscriberAddress;

    @Column(name = "protocol_subscriber_port")
    private Integer protocolSubscriberPort;

    @Column(name = "protocol_subscriber_uri")
    private String protocolSubscriberUri;

    @Column(name = "protocol_subscriber_stream_id")
    private Integer protocolSubscriberStreamId;

    @Column(name = "metric_producer_address")
    private String metricProducerAddress;

    @Column(name = "metric_producer_port")
    private Integer metricProducerPort;

    @Column(name = "metric_producer_uri")
    private String metricProducerUri;

    @Column(name = "metric_producer_stream_id")
    private Integer metricProducerStreamId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "realAlerts", "metricSubscribers", "client", "metricTagsValue" }, allowSetters = true)
    private Set<Metric> metrics = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "client", "metric" }, allowSetters = true)
    private Set<MetricSubscriber> metricSubscribers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "alertSubscribers", "client", "metric" }, allowSetters = true)
    private Set<RealAlert> realAlerts = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "client", "realAlert" }, allowSetters = true)
    private Set<AlertSubscriber> alertSubscribers = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "clients", "metricTagsValues", "metricMeta" }, allowSetters = true)
    private Tenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Integer getId() {
        return this.id;
    }

    public Client id(Integer id) {
        this.setId(id);
        return this;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProtocolProducerAddress() {
        return this.protocolProducerAddress;
    }

    public Client protocolProducerAddress(String protocolProducerAddress) {
        this.setProtocolProducerAddress(protocolProducerAddress);
        return this;
    }

    public void setProtocolProducerAddress(String protocolProducerAddress) {
        this.protocolProducerAddress = protocolProducerAddress;
    }

    public Integer getProtocolProducerPort() {
        return this.protocolProducerPort;
    }

    public Client protocolProducerPort(Integer protocolProducerPort) {
        this.setProtocolProducerPort(protocolProducerPort);
        return this;
    }

    public void setProtocolProducerPort(Integer protocolProducerPort) {
        this.protocolProducerPort = protocolProducerPort;
    }

    public String getProtocolProducerUri() {
        return this.protocolProducerUri;
    }

    public Client protocolProducerUri(String protocolProducerUri) {
        this.setProtocolProducerUri(protocolProducerUri);
        return this;
    }

    public void setProtocolProducerUri(String protocolProducerUri) {
        this.protocolProducerUri = protocolProducerUri;
    }

    public Integer getProtocolProducerStreamId() {
        return this.protocolProducerStreamId;
    }

    public Client protocolProducerStreamId(Integer protocolProducerStreamId) {
        this.setProtocolProducerStreamId(protocolProducerStreamId);
        return this;
    }

    public void setProtocolProducerStreamId(Integer protocolProducerStreamId) {
        this.protocolProducerStreamId = protocolProducerStreamId;
    }

    public String getProtocolSubscriberAddress() {
        return this.protocolSubscriberAddress;
    }

    public Client protocolSubscriberAddress(String protocolSubscriberAddress) {
        this.setProtocolSubscriberAddress(protocolSubscriberAddress);
        return this;
    }

    public void setProtocolSubscriberAddress(String protocolSubscriberAddress) {
        this.protocolSubscriberAddress = protocolSubscriberAddress;
    }

    public Integer getProtocolSubscriberPort() {
        return this.protocolSubscriberPort;
    }

    public Client protocolSubscriberPort(Integer protocolSubscriberPort) {
        this.setProtocolSubscriberPort(protocolSubscriberPort);
        return this;
    }

    public void setProtocolSubscriberPort(Integer protocolSubscriberPort) {
        this.protocolSubscriberPort = protocolSubscriberPort;
    }

    public String getProtocolSubscriberUri() {
        return this.protocolSubscriberUri;
    }

    public Client protocolSubscriberUri(String protocolSubscriberUri) {
        this.setProtocolSubscriberUri(protocolSubscriberUri);
        return this;
    }

    public void setProtocolSubscriberUri(String protocolSubscriberUri) {
        this.protocolSubscriberUri = protocolSubscriberUri;
    }

    public Integer getProtocolSubscriberStreamId() {
        return this.protocolSubscriberStreamId;
    }

    public Client protocolSubscriberStreamId(Integer protocolSubscriberStreamId) {
        this.setProtocolSubscriberStreamId(protocolSubscriberStreamId);
        return this;
    }

    public void setProtocolSubscriberStreamId(Integer protocolSubscriberStreamId) {
        this.protocolSubscriberStreamId = protocolSubscriberStreamId;
    }

    public String getMetricProducerAddress() {
        return this.metricProducerAddress;
    }

    public Client metricProducerAddress(String metricProducerAddress) {
        this.setMetricProducerAddress(metricProducerAddress);
        return this;
    }

    public void setMetricProducerAddress(String metricProducerAddress) {
        this.metricProducerAddress = metricProducerAddress;
    }

    public Integer getMetricProducerPort() {
        return this.metricProducerPort;
    }

    public Client metricProducerPort(Integer metricProducerPort) {
        this.setMetricProducerPort(metricProducerPort);
        return this;
    }

    public void setMetricProducerPort(Integer metricProducerPort) {
        this.metricProducerPort = metricProducerPort;
    }

    public String getMetricProducerUri() {
        return this.metricProducerUri;
    }

    public Client metricProducerUri(String metricProducerUri) {
        this.setMetricProducerUri(metricProducerUri);
        return this;
    }

    public void setMetricProducerUri(String metricProducerUri) {
        this.metricProducerUri = metricProducerUri;
    }

    public Integer getMetricProducerStreamId() {
        return this.metricProducerStreamId;
    }

    public Client metricProducerStreamId(Integer metricProducerStreamId) {
        this.setMetricProducerStreamId(metricProducerStreamId);
        return this;
    }

    public void setMetricProducerStreamId(Integer metricProducerStreamId) {
        this.metricProducerStreamId = metricProducerStreamId;
    }

    public Set<Metric> getMetrics() {
        return this.metrics;
    }

    public void setMetrics(Set<Metric> metrics) {
        if (this.metrics != null) {
            this.metrics.forEach(i -> i.setClient(null));
        }
        if (metrics != null) {
            metrics.forEach(i -> i.setClient(this));
        }
        this.metrics = metrics;
    }

    public Client metrics(Set<Metric> metrics) {
        this.setMetrics(metrics);
        return this;
    }

    public Client addMetric(Metric metric) {
        this.metrics.add(metric);
        metric.setClient(this);
        return this;
    }

    public Client removeMetric(Metric metric) {
        this.metrics.remove(metric);
        metric.setClient(null);
        return this;
    }

    public Set<MetricSubscriber> getMetricSubscribers() {
        return this.metricSubscribers;
    }

    public void setMetricSubscribers(Set<MetricSubscriber> metricSubscribers) {
        if (this.metricSubscribers != null) {
            this.metricSubscribers.forEach(i -> i.setClient(null));
        }
        if (metricSubscribers != null) {
            metricSubscribers.forEach(i -> i.setClient(this));
        }
        this.metricSubscribers = metricSubscribers;
    }

    public Client metricSubscribers(Set<MetricSubscriber> metricSubscribers) {
        this.setMetricSubscribers(metricSubscribers);
        return this;
    }

    public Client addMetricSubscriber(MetricSubscriber metricSubscriber) {
        this.metricSubscribers.add(metricSubscriber);
        metricSubscriber.setClient(this);
        return this;
    }

    public Client removeMetricSubscriber(MetricSubscriber metricSubscriber) {
        this.metricSubscribers.remove(metricSubscriber);
        metricSubscriber.setClient(null);
        return this;
    }

    public Set<RealAlert> getRealAlerts() {
        return this.realAlerts;
    }

    public void setRealAlerts(Set<RealAlert> realAlerts) {
        if (this.realAlerts != null) {
            this.realAlerts.forEach(i -> i.setClient(null));
        }
        if (realAlerts != null) {
            realAlerts.forEach(i -> i.setClient(this));
        }
        this.realAlerts = realAlerts;
    }

    public Client realAlerts(Set<RealAlert> realAlerts) {
        this.setRealAlerts(realAlerts);
        return this;
    }

    public Client addRealAlert(RealAlert realAlert) {
        this.realAlerts.add(realAlert);
        realAlert.setClient(this);
        return this;
    }

    public Client removeRealAlert(RealAlert realAlert) {
        this.realAlerts.remove(realAlert);
        realAlert.setClient(null);
        return this;
    }

    public Set<AlertSubscriber> getAlertSubscribers() {
        return this.alertSubscribers;
    }

    public void setAlertSubscribers(Set<AlertSubscriber> alertSubscribers) {
        if (this.alertSubscribers != null) {
            this.alertSubscribers.forEach(i -> i.setClient(null));
        }
        if (alertSubscribers != null) {
            alertSubscribers.forEach(i -> i.setClient(this));
        }
        this.alertSubscribers = alertSubscribers;
    }

    public Client alertSubscribers(Set<AlertSubscriber> alertSubscribers) {
        this.setAlertSubscribers(alertSubscribers);
        return this;
    }

    public Client addAlertSubscriber(AlertSubscriber alertSubscriber) {
        this.alertSubscribers.add(alertSubscriber);
        alertSubscriber.setClient(this);
        return this;
    }

    public Client removeAlertSubscriber(AlertSubscriber alertSubscriber) {
        this.alertSubscribers.remove(alertSubscriber);
        alertSubscriber.setClient(null);
        return this;
    }

    public Tenant getTenant() {
        return this.tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Client tenant(Tenant tenant) {
        this.setTenant(tenant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }
        return getId() != null && getId().equals(((Client) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Client{" +
            "id=" + getId() +
            ", protocolProducerAddress='" + getProtocolProducerAddress() + "'" +
            ", protocolProducerPort=" + getProtocolProducerPort() +
            ", protocolProducerUri='" + getProtocolProducerUri() + "'" +
            ", protocolProducerStreamId=" + getProtocolProducerStreamId() +
            ", protocolSubscriberAddress='" + getProtocolSubscriberAddress() + "'" +
            ", protocolSubscriberPort=" + getProtocolSubscriberPort() +
            ", protocolSubscriberUri='" + getProtocolSubscriberUri() + "'" +
            ", protocolSubscriberStreamId=" + getProtocolSubscriberStreamId() +
            ", metricProducerAddress='" + getMetricProducerAddress() + "'" +
            ", metricProducerPort=" + getMetricProducerPort() +
            ", metricProducerUri='" + getMetricProducerUri() + "'" +
            ", metricProducerStreamId=" + getMetricProducerStreamId() +
            "}";
    }
}
