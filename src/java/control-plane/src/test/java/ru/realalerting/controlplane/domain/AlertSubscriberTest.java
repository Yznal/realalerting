package ru.realalerting.controlplane.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.realalerting.controlplane.domain.AlertSubscriberTestSamples.*;
import static ru.realalerting.controlplane.domain.ClientTestSamples.*;
import static ru.realalerting.controlplane.domain.RealAlertTestSamples.*;

import org.junit.jupiter.api.Test;
import ru.realalerting.controlplane.web.rest.TestUtil;

class AlertSubscriberTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AlertSubscriber.class);
        AlertSubscriber alertSubscriber1 = getAlertSubscriberSample1();
        AlertSubscriber alertSubscriber2 = new AlertSubscriber();
        assertThat(alertSubscriber1).isNotEqualTo(alertSubscriber2);

        alertSubscriber2.setId(alertSubscriber1.getId());
        assertThat(alertSubscriber1).isEqualTo(alertSubscriber2);

        alertSubscriber2 = getAlertSubscriberSample2();
        assertThat(alertSubscriber1).isNotEqualTo(alertSubscriber2);
    }

    @Test
    void clientTest() throws Exception {
        AlertSubscriber alertSubscriber = getAlertSubscriberRandomSampleGenerator();
        Client clientBack = getClientRandomSampleGenerator();

        alertSubscriber.setClient(clientBack);
        assertThat(alertSubscriber.getClient()).isEqualTo(clientBack);

        alertSubscriber.client(null);
        assertThat(alertSubscriber.getClient()).isNull();
    }

    @Test
    void realAlertTest() throws Exception {
        AlertSubscriber alertSubscriber = getAlertSubscriberRandomSampleGenerator();
        RealAlert realAlertBack = getRealAlertRandomSampleGenerator();

        alertSubscriber.setRealAlert(realAlertBack);
        assertThat(alertSubscriber.getRealAlert()).isEqualTo(realAlertBack);

        alertSubscriber.realAlert(null);
        assertThat(alertSubscriber.getRealAlert()).isNull();
    }
}
