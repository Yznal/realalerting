package ru.realalerting.controlplane.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A MetricMeta.
 */
@Entity
@Table(name = "metric_meta")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MetricMeta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "label_01")
    private String label01;

    @Column(name = "label_256")
    private String label256;

    @JsonIgnoreProperties(value = { "clients", "metricTagsValues", "metricMeta" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private Tenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MetricMeta id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel01() {
        return this.label01;
    }

    public MetricMeta label01(String label01) {
        this.setLabel01(label01);
        return this;
    }

    public void setLabel01(String label01) {
        this.label01 = label01;
    }

    public String getLabel256() {
        return this.label256;
    }

    public MetricMeta label256(String label256) {
        this.setLabel256(label256);
        return this;
    }

    public void setLabel256(String label256) {
        this.label256 = label256;
    }

    public Tenant getTenant() {
        return this.tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public MetricMeta tenant(Tenant tenant) {
        this.setTenant(tenant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MetricMeta)) {
            return false;
        }
        return getId() != null && getId().equals(((MetricMeta) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MetricMeta{" +
            "id=" + getId() +
            ", label01='" + getLabel01() + "'" +
            ", label256='" + getLabel256() + "'" +
            "}";
    }
}
