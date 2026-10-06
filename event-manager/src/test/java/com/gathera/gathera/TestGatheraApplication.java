package com.gathera.gathera;

import org.springframework.boot.SpringApplication;

public class TestGatheraApplication {

	public static void main(String[] args) {
		SpringApplication.from(GatheraApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
