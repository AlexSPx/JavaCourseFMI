package bg.sofia.uni.fmi.mjt.order.server.order;

import bg.sofia.uni.fmi.mjt.order.server.destination.Destination;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Color;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Size;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.TShirt;

public record Order(int id, TShirt tShirt, Destination destination) {

    public String generateInfo() {
        if (this.tShirt().size() == Size.UNKNOWN ||
                this.tShirt().color() == Color.UNKNOWN ||
                this.destination() == Destination.UNKNOWN) {

            StringBuilder additionalInfo = new StringBuilder("invalid=");

            if (this.tShirt().size() == Size.UNKNOWN) {
                additionalInfo.append("size,");
            }

            if (this.tShirt().color() == Color.UNKNOWN) {
                additionalInfo.append("color,");
            }

            if (this.destination() == Destination.UNKNOWN) {
                additionalInfo.append("destination,");
            }

            if (additionalInfo.charAt(additionalInfo.length() - 1) == ',') {
                additionalInfo.deleteCharAt(additionalInfo.length() - 1);
            }

            return additionalInfo.toString();
        } else {
            return String.format("ORDER_ID=%s", this.id);
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + "\", " +
                "\"tShirt\":" + tShirt + ", " +
                "\"destination\":\"" + destination + "\"" +
                "}";    }
}
