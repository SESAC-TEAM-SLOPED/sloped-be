package org.sesac.slopedbe.road;

import org.sesac.slopedbe.road.repository.RoadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class RoadRepositoryTest {

	@Autowired
	private RoadRepository roadRepository;

}
