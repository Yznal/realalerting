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
 * A Tenant.
 */
@Entity
@Table(name = "tenant")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Tenant implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "metrics", "metricSubscribers", "realAlerts", "alertSubscribers", "tenant" }, allowSetters = true)
    private Set<Client> clients = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "metric", "tenant" }, allowSetters = true)
    private Set<MetricTagsValue> metricTagsValues = new HashSet<>();

    @JsonIgnoreProperties(value = { "tenant" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "tenant")
    private MetricMeta metricMeta;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Integer getId() {
        return this.id;
    }

    public Tenant id(Integer id) {
        this.setId(id);
        return this;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Tenant name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Tenant description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Client> getClients() {
        return this.clients;
    }

    public void setClients(Set<Client> clients) {
        if (this.clients != null) {
            this.clients.forEach(i -> i.setTenant(null));
        }
        if (clients != null) {
            clients.forEach(i -> i.setTenant(this));
        }
        this.clients = clients;
    }

    public Tenant clients(Set<Client> clients) {
        this.setClients(clients);
        return this;
    }

    public Tenant addClient(Client client) {
        this.clients.add(client);
        client.setTenant(this);
        return this;
    }

    public Tenant removeClient(Client client) {
        this.clients.remove(client);
        client.setTenant(null);
        return this;
    }

    public Set<MetricTagsValue> getMetricTagsValues() {
        return this.metricTagsValues;
    }

    public void setMetricTagsValues(Set<MetricTagsValue> metricTagsValues) {
        if (this.metricTagsValues != null) {
            this.metricTagsValues.forEach(i -> i.setTenant(null));
        }
        if (metricTagsValues != null) {
            metricTagsValues.forEach(i -> i.setTenant(this));
        }
        this.metricTagsValues = metricTagsValues;
    }

    public Tenant metricTagsValues(Set<MetricTagsValue> metricTagsValues) {
        this.setMetricTagsValues(metricTagsValues);
        return this;
    }

    public Tenant addMetricTagsValue(MetricTagsValue metricTagsValue) {
        this.metricTagsValues.add(metricTagsValue);
        metricTagsValue.setTenant(this);
        return this;
    }

    public Tenant removeMetricTagsValue(MetricTagsValue metricTagsValue) {
        this.metricTagsValues.remove(metricTagsValue);
        metricTagsValue.setTenant(null);
        return this;
    }

    public MetricMeta getMetricMeta() {
        return this.metricMeta;
    }

    public void setMetricMeta(MetricMeta metricMeta) {
        if (this.metricMeta != null) {
            this.metricMeta.setTenant(null);
        }
        if (metricMeta != null) {
            metricMeta.setTenant(this);
        }
        this.metricMeta = metricMeta;
    }

    public Tenant metricMeta(MetricMeta metricMeta) {
        this.setMetricMeta(metricMeta);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tenant)) {
            return false;
        }
        return getId() != null && getId().equals(((Tenant) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tenant{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
