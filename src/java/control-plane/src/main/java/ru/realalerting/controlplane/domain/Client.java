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

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "protocol_address")
    private String protocolAddress;

    @Column(name = "protocol_port")
    private Integer protocolPort;

    @Column(name = "protocol_uri")
    private String protocolUri;

    @Column(name = "protocol_stream_id")
    private Integer protocolStreamId;

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
    @JsonIgnoreProperties(value = { "alerts", "metricSubscribers", "client", "metricTagsValue" }, allowSetters = true)
    private Set<Metric> metrics = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "client", "metric" }, allowSetters = true)
    private Set<MetricSubscriber> metricSubscribers = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "clients", "metricTagsValues", "metricMeta" }, allowSetters = true)
    private Tenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Client id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProtocolAddress() {
        return this.protocolAddress;
    }

    public Client protocolAddress(String protocolAddress) {
        this.setProtocolAddress(protocolAddress);
        return this;
    }

    public void setProtocolAddress(String protocolAddress) {
        this.protocolAddress = protocolAddress;
    }

    public Integer getProtocolPort() {
        return this.protocolPort;
    }

    public Client protocolPort(Integer protocolPort) {
        this.setProtocolPort(protocolPort);
        return this;
    }

    public void setProtocolPort(Integer protocolPort) {
        this.protocolPort = protocolPort;
    }

    public String getProtocolUri() {
        return this.protocolUri;
    }

    public Client protocolUri(String protocolUri) {
        this.setProtocolUri(protocolUri);
        return this;
    }

    public void setProtocolUri(String protocolUri) {
        this.protocolUri = protocolUri;
    }

    public Integer getProtocolStreamId() {
        return this.protocolStreamId;
    }

    public Client protocolStreamId(Integer protocolStreamId) {
        this.setProtocolStreamId(protocolStreamId);
        return this;
    }

    public void setProtocolStreamId(Integer protocolStreamId) {
        this.protocolStreamId = protocolStreamId;
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
            ", protocolAddress='" + getProtocolAddress() + "'" +
            ", protocolPort=" + getProtocolPort() +
            ", protocolUri='" + getProtocolUri() + "'" +
            ", protocolStreamId=" + getProtocolStreamId() +
            ", metricProducerAddress='" + getMetricProducerAddress() + "'" +
            ", metricProducerPort=" + getMetricProducerPort() +
            ", metricProducerUri='" + getMetricProducerUri() + "'" +
            ", metricProducerStreamId=" + getMetricProducerStreamId() +
            "}";
    }
}
