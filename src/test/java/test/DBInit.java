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
package test;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import test.model.Car;
import test.model.Engine;
import test.model.GasTank;
import test.model.Motor;
import test.model.Wheel;
import test.repository.CarRepository;
import test.repository.EngineRepository;
import test.repository.GasTankRepository;
import test.repository.MotorRepository;
import test.repository.WheelRepository;

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

  @PostConstruct
  void insertDocuments() {
    carRepo.deleteAll();
    engineRepo.deleteAll();
    gasTankRepo.deleteAll();
    motorRepo.deleteAll();
    wheelRepo.deleteAll();

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

    car.getWheels().add(new Wheel("Michelin"));
    car.getWheels().add(new Wheel("Goodyear"));
    car.getWheels().add(new Wheel("Continental"));
    car.getWheels().add(new Wheel("Bridgestone"));

    carRepo.save(car);

    System.out.println(car.getEngine().getId());
    System.out.println(car.getEngine().getMotor().getId());
    System.out.println(car.getGasTank().getId());
    System.out.println(car.getWheels().get(0).getId());
    System.out.println(car.getWheels().get(1).getId());
    System.out.println(car.getWheels().get(2).getId());
    System.out.println(car.getWheels().get(3).getId());
    car = carRepo.findAll().get(0);
    carRepo.delete(car);
  }

}
