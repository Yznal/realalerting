package ru.realalerting.controlplane.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A MetricSubscriber.
 */
@Entity
@Table(name = "metric_subscriber")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MetricSubscriber implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "subscriber_address")
    private String subscriberAddress;

    @Column(name = "subscriber_port")
    private Integer subscriberPort;

    @Column(name = "subscriber_uri")
    private String subscriberUri;

    @Column(name = "subscriber_stream_id")
    private Integer subscriberStreamId;

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

    public MetricSubscriber id(Integer id) {
        this.setId(id);
        return this;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubscriberAddress() {
        return this.subscriberAddress;
    }

    public MetricSubscriber subscriberAddress(String subscriberAddress) {
        this.setSubscriberAddress(subscriberAddress);
        return this;
    }

    public void setSubscriberAddress(String subscriberAddress) {
        this.subscriberAddress = subscriberAddress;
    }

    public Integer getSubscriberPort() {
        return this.subscriberPort;
    }

    public MetricSubscriber subscriberPort(Integer subscriberPort) {
        this.setSubscriberPort(subscriberPort);
        return this;
    }

    public void setSubscriberPort(Integer subscriberPort) {
        this.subscriberPort = subscriberPort;
    }

    public String getSubscriberUri() {
        return this.subscriberUri;
    }

    public MetricSubscriber subscriberUri(String subscriberUri) {
        this.setSubscriberUri(subscriberUri);
        return this;
    }

    public void setSubscriberUri(String subscriberUri) {
        this.subscriberUri = subscriberUri;
    }

    public Integer getSubscriberStreamId() {
        return this.subscriberStreamId;
    }

    public MetricSubscriber subscriberStreamId(Integer subscriberStreamId) {
        this.setSubscriberStreamId(subscriberStreamId);
        return this;
    }

    public void setSubscriberStreamId(Integer subscriberStreamId) {
        this.subscriberStreamId = subscriberStreamId;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public MetricSubscriber client(Client client) {
        this.setClient(client);
        return this;
    }

    public Metric getMetric() {
        return this.metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public MetricSubscriber metric(Metric metric) {
        this.setMetric(metric);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MetricSubscriber)) {
            return false;
        }
        return getId() != null && getId().equals(((MetricSubscriber) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MetricSubscriber{" +
            "id=" + getId() +
            ", subscriberAddress='" + getSubscriberAddress() + "'" +
            ", subscriberPort=" + getSubscriberPort() +
            ", subscriberUri='" + getSubscriberUri() + "'" +
            ", subscriberStreamId=" + getSubscriberStreamId() +
            "}";
    }
}
