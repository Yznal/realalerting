package ru.realalerting.controlplane.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A MetricTagsValue.
 */
@Entity
@Table(name = "metric_tags_value")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MetricTagsValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "value_1")
    private String value1;

    @Column(name = "value_256")
    private String value256;

    @JsonIgnoreProperties(value = { "realAlerts", "metricSubscribers", "client", "metricTagsValue" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private Metric metric;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "clients", "metricTagsValues", "metricMeta" }, allowSetters = true)
    private Tenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MetricTagsValue id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue1() {
        return this.value1;
    }

    public MetricTagsValue value1(String value1) {
        this.setValue1(value1);
        return this;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue256() {
        return this.value256;
    }

    public MetricTagsValue value256(String value256) {
        this.setValue256(value256);
        return this;
    }

    public void setValue256(String value256) {
        this.value256 = value256;
    }

    public Metric getMetric() {
        return this.metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public MetricTagsValue metric(Metric metric) {
        this.setMetric(metric);
        return this;
    }

    public Tenant getTenant() {
        return this.tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public MetricTagsValue tenant(Tenant tenant) {
        this.setTenant(tenant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MetricTagsValue)) {
            return false;
        }
        return getId() != null && getId().equals(((MetricTagsValue) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MetricTagsValue{" +
            "id=" + getId() +
            ", value1='" + getValue1() + "'" +
            ", value256='" + getValue256() + "'" +
            "}";
    }
}
