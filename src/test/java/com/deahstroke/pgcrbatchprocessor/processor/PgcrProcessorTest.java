package com.deahstroke.pgcrbatchprocessor.processor;

import static org.assertj.core.api.Assertions.assertThat;

import com.deahstroke.pgcrbatchprocessor.dto.ActivityDetails;
import com.deahstroke.pgcrbatchprocessor.dto.Basic;
import com.deahstroke.pgcrbatchprocessor.dto.DestinyUserInfo;
import com.deahstroke.pgcrbatchprocessor.dto.DisplayProperties;
import com.deahstroke.pgcrbatchprocessor.dto.ManifestResponse;
import com.deahstroke.pgcrbatchprocessor.dto.PlayerEntry;
import com.deahstroke.pgcrbatchprocessor.dto.PlayerStatsValues;
import com.deahstroke.pgcrbatchprocessor.dto.PostGameCarnageReport;
import com.deahstroke.pgcrbatchprocessor.dto.PostGameCarnageReportEntry;
import com.deahstroke.pgcrbatchprocessor.enums.CharacterClass;
import com.deahstroke.pgcrbatchprocessor.enums.CharacterGender;
import com.deahstroke.pgcrbatchprocessor.enums.CharacterRace;
import com.deahstroke.pgcrbatchprocessor.service.ManifestMarshallingService;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PgcrProcessorTest {

  @Mock
  ManifestMarshallingService manifestMarshallingService;

  @InjectMocks
  PgcrProcessor sut;

  private static Stream<Arguments> provideTestData() {
    return Stream.of(
        Arguments.of(new PlayerStatsValues(1, 1, 1, 1, 0, 12.0, 0.2, 1.0, 1,
            213, null, 12, 231, 1)),
        Arguments.of(new PlayerStatsValues(
            new Basic(1.0, "1"), new Basic(1.0, "Yes"), new Basic(1.0, "1"),
            new Basic(1.0, "1"), new Basic(1.0, "1"), new Basic(12.0, "12.0"),
            new Basic(0.2, "0.2"), new Basic(1.0, "1.0"), new Basic(1.0, "1.0"),
            new Basic(213.0, "213"), new Basic(1.0, "?"), new Basic(12.0, "12"),
            new Basic(231.0, "231"), new Basic(1.0, "1"))
        )
    );
  }

  @ParameterizedTest
  @MethodSource("provideTestData")
  void shouldProcessCompletionsSuccesfully(PlayerStatsValues playerStatsValues) {
    // given: pgcr with a completion
    ActivityDetails activityDetails = new ActivityDetails(1L, 2L, "3", 4, Collections.emptyList(),
        false, 1);
    PlayerEntry player = new PlayerEntry(
        new DestinyUserInfo("", 1, 12L, "Deaht", false, null, null), "Awoken", 2412L, 318245L,
        12318L, 9501L, 30);
    List<PostGameCarnageReportEntry> entryLists = List.of(
        new PostGameCarnageReportEntry(1, player, playerStatsValues, null, 1L)
    );
    PostGameCarnageReport item = new PostGameCarnageReport(
        Instant.now(),
        0,
        null,
        activityDetails,
        entryLists
    );

    var manifestResponse = Optional.of(
        new ManifestResponse(new DisplayProperties("", "Last Wish", "", "", false), null, null,
            null, null, null, null, null));
    Mockito.when(manifestMarshallingService.getManifest(
            String.valueOf(item.activityDetails().directorActivityHash())))
        .thenReturn(manifestResponse);

    Mockito.when(manifestMarshallingService.getLabeled(
            String.valueOf(item.entries().getFirst().player().raceHash()), CharacterRace.class))
        .thenReturn(CharacterRace.AWOKEN);
    Mockito.when(manifestMarshallingService.getLabeled(
            String.valueOf(item.entries().getFirst().player().classHash()), CharacterClass.class))
        .thenReturn(CharacterClass.HUNTER);
    Mockito.when(manifestMarshallingService.getLabeled(
            String.valueOf(item.entries().getFirst().player().genderHash()), CharacterGender.class))
        .thenReturn(CharacterGender.FEMALE);

    // when: process is called
    var response = sut.process(item);

    // then: the response has correct params
    assertThat(response).isNotNull();
    assertThat(response.playerInformation()).hasSize(1);

    var onlyElement = response.playerInformation().stream().toList().getFirst();
    assertThat(onlyElement.isPublic()).isFalse();
    assertThat(onlyElement.characterInformation().weaponInformation()).isEmpty();
    assertThat(onlyElement.characterInformation().activityCompleted()).isTrue();
  }

}
