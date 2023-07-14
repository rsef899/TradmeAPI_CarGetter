public class Main {

	public static void main(String[] args) {

		String[] exteriorColour = { "Blue", "Red", "Black", "White", "Silver" };
		String[] makes = { "Audi", "BMW" };
		String[] bodyStyle = { "Coupe", "Hatchback" };
		Controller.callGetCars(makes, exteriorColour, bodyStyle, 31, false, Authentication.readKeys());
	}

}
