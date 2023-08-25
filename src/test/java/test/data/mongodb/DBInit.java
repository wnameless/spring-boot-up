/*
 *
 * Copyright 2021 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package test.data.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import test.data.mongodb.model.Car;
import test.data.mongodb.model.Engine;
import test.data.mongodb.model.GasTank;
import test.data.mongodb.model.Motor;
import test.data.mongodb.model.Wheel;
import test.data.mongodb.repository.CarRepository;
import test.data.mongodb.repository.EngineRepository;
import test.data.mongodb.repository.GasTankRepository;
import test.data.mongodb.repository.MotorRepository;
import test.data.mongodb.repository.WheelRepository;

@Component
public class DBInit {

  @Autowired
  CarRepository carRepo;
  @Autowired
  EngineRepository engineRepo;
  @Autowired
  GasTankRepository gasTankRepo;
  @Autowired
  MotorRepository motorRepo;
  @Autowired
  WheelRepository wheelRepo;

  @EventListener(ApplicationReadyEvent.class)
  void insertDocuments() {
    carRepo.deleteAll();
    engineRepo.deleteAll();
    gasTankRepo.deleteAll();
    motorRepo.deleteAll();
    wheelRepo.deleteAll();

    for (int i = 0; i < 100; i++) {
      Car car = new Car();

      Engine engine = new Engine();
      engine.setHorsePower(500);

      Motor motor = new Motor();
      motor.setRpm(60000);
      engine.setMotor(motor);

      GasTank gasTank = new GasTank();
      gasTank.setCapacity(100);

      car.setEngine(engine);
      car.setGasTank(gasTank);

      car.getFrontWheels().add(new Wheel("Michelin"));
      car.getFrontWheels().add(new Wheel("Goodyear"));
      car.getRareWheels().put("rareLeft", new Wheel("Continental"));
      car.getRareWheels().put("rareRight", new Wheel("Bridgestone"));

      carRepo.save(car);
    }

    carRepo.deleteAll(carRepo.findAll());
  }

}
