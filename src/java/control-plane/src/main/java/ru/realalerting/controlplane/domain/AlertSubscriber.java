package ru.realalerting.controlplane.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AlertSubscriber.
 */
@Entity
@Table(name = "alert_subscriber")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AlertSubscriber implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

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
    @JsonIgnoreProperties(value = { "metric" }, allowSetters = true)
    private Alert alert;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "metrics", "metricSubscribers", "tenant" }, allowSetters = true)
    private Client client;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AlertSubscriber id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubscriberAddress() {
        return this.subscriberAddress;
    }

    public AlertSubscriber subscriberAddress(String subscriberAddress) {
        this.setSubscriberAddress(subscriberAddress);
        return this;
    }

    public void setSubscriberAddress(String subscriberAddress) {
        this.subscriberAddress = subscriberAddress;
    }

    public Integer getSubscriberPort() {
        return this.subscriberPort;
    }

    public AlertSubscriber subscriberPort(Integer subscriberPort) {
        this.setSubscriberPort(subscriberPort);
        return this;
    }

    public void setSubscriberPort(Integer subscriberPort) {
        this.subscriberPort = subscriberPort;
    }

    public String getSubscriberUri() {
        return this.subscriberUri;
    }

    public AlertSubscriber subscriberUri(String subscriberUri) {
        this.setSubscriberUri(subscriberUri);
        return this;
    }

    public void setSubscriberUri(String subscriberUri) {
        this.subscriberUri = subscriberUri;
    }

    public Integer getSubscriberStreamId() {
        return this.subscriberStreamId;
    }

    public AlertSubscriber subscriberStreamId(Integer subscriberStreamId) {
        this.setSubscriberStreamId(subscriberStreamId);
        return this;
    }

    public void setSubscriberStreamId(Integer subscriberStreamId) {
        this.subscriberStreamId = subscriberStreamId;
    }

    public Alert getAlert() {
        return this.alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public AlertSubscriber alert(Alert alert) {
        this.setAlert(alert);
        return this;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public AlertSubscriber client(Client client) {
        this.setClient(client);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AlertSubscriber)) {
            return false;
        }
        return getId() != null && getId().equals(((AlertSubscriber) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AlertSubscriber{" +
            "id=" + getId() +
            ", subscriberAddress='" + getSubscriberAddress() + "'" +
            ", subscriberPort=" + getSubscriberPort() +
            ", subscriberUri='" + getSubscriberUri() + "'" +
            ", subscriberStreamId=" + getSubscriberStreamId() +
            "}";
    }
}
