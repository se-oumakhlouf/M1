package fr.uge.concurrence.deadlock;

import java.util.List;

public class Application {
	public static void main(String[] args) throws InterruptedException {
		var rooms = List.of("bedroom1", "bedroom2", "kitchen", "dining-room", "bathroom", "toilets");

		var temperature = new Temperature(rooms.size());

		for (int i = 0; i < rooms.size(); i++) {
			var index = i;
			Thread.ofPlatform().start(() -> {
				for (;;) {
					try {
						var room = rooms.get(index);
						var temp = Heat4J.retrieveTemperature(room);
						System.out.println("Temperature in room " + room + " : " + temp);
						temperature.add(temp);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}

		for (;;)
			System.out.println("Average : " + temperature.averageTemperatures() + "\n");
	}
}