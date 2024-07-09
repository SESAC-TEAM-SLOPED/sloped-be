package org.sesac.slopedbe.road;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.sesac.slopedbe.road.model.entity.Road;
import org.sesac.slopedbe.road.repository.RoadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
@ActiveProfiles("test")
class RoadRepositoryTest {

	@Autowired
	private RoadRepository roadRepository;

	@Test
	public void testSaveRoad() {
		BigDecimal latitude = new BigDecimal("37.7749");
		BigDecimal longitude = new BigDecimal("-122.4194");
		String content = "This is a road.";
		String address = "123 Road St.";

		Road road = Road.createRoad(latitude, longitude, content, address);

		// 엔티티 저장
		Road savedRoad = roadRepository.save(road);

		// 저장된 엔티티 확인
		assertThat(savedRoad).isNotNull();
		assertThat(savedRoad.getId()).isNotNull();
		assertThat(savedRoad.getPoint()).isInstanceOf(Point.class);
		assertThat(savedRoad.getContent()).isEqualTo(content);
		assertThat(savedRoad.getAddress()).isEqualTo(address);

		// 저장된 엔티티 출력
		System.out.println("Saved Road ID: " + savedRoad.getId());
		System.out.println("Point: " + savedRoad.getPoint());
		System.out.println("Content: " + savedRoad.getContent());
		System.out.println("Address: " + savedRoad.getAddress());
	}
}
