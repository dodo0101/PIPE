package uk.ac.imperial.pipe.models;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.DiscretePlace;
import uk.ac.imperial.pipe.models.petrinet.DiscretePlaceVisitor;
import uk.ac.imperial.pipe.models.petrinet.PlaceVisitor;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DiscretePlaceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    DiscretePlace place;

    @Before
    public void setUp() {
        place = new DiscretePlace("test", "test");
    }

    @Test
    public void placeObjectIsSelectable() {
        assertTrue(place.isSelectable());
    }

    @Test
    public void calculatesCorrectArcAttachmentPointsDirectlyAbove() {
        int x1 = 0;
        int y1 = 0;

        place.setX(x1);
        place.setY(y1);

        Point2D.Double point = place.getArcEdgePoint(Math.toRadians(90));
        Point2D.Double expected = new Point2D.Double(15, 0);
        assertEquals(expected.x, point.x, 0.001);
        assertEquals(expected.y, point.y, 0.001);
    }

    @Test
    public void visitsDiscretePlaceVisitor() throws PetriNetComponentException {
        DiscretePlaceVisitor visitor = mock(DiscretePlaceVisitor.class);
        place.accept(visitor);
        verify(visitor).visit(place);
    }


    @Test
    public void visitsPlaceVisitor() throws PetriNetComponentException {
        PlaceVisitor visitor = mock(PlaceVisitor.class);
        place.accept(visitor);
        verify(visitor).visit(place);
    }


    @Test
    public void calculatesCorrectArcAttachmentPointsDirectlyBelow() {
        int x1 = 0;
        int y1 = 0;

        place.setX(x1);
        place.setY(y1);

        Point2D.Double point = place.getArcEdgePoint(Math.toRadians(-90));
        Point2D.Double expected = new Point2D.Double(15, 30);
        assertEquals(expected.x, point.x, 0.001);
        assertEquals(expected.y, point.y, 0.001);
    }

    private double getAngleBetweenObjects(double x1, double y1, double x2, double y2) {
        double deltax = x1 - x2;
        double deltay = y1 - y2;
        return Math.atan2(deltax, deltay);
    }

    @Test
    public void calculatesCorrectArcAttachmentPointsDirectlyRight() {
        int x1 = 0;
        int y1 = 0;

        place.setX(x1);
        place.setY(y1);

        Point2D.Double point = place.getArcEdgePoint(Math.toRadians(180));
        Point2D.Double expected = new Point2D.Double(30, 15);
        assertEquals(expected.x, point.x, 0.001);
        assertEquals(expected.y, point.y, 0.001);
    }

    @Test
    public void calculatesCorrectArcAttachmentPointsDirectlyLeft() {
        int x1 = 0;
        int y1 = 0;

        place.setX(x1);
        place.setY(y1);

        Point2D.Double point = place.getArcEdgePoint(0);
        Point2D.Double expected = new Point2D.Double(0, 15);
        assertEquals(expected.x, point.x, 0.001);
        assertEquals(expected.y, point.y, 0.001);
    }

    /**
     * I.e. A->B we're calculating B
     */
    @Test
    public void calculatesCorrectArcAttachmentPointsAsSource() {
        Point source = new Point(0, 0);
        Point target = new Point(30, 30);
        double angle = getAngleBetweenObjects(source.x, source.y, target.x, target.y);

        place.setX(source.x);
        place.setY(source.y);

        double FORTY_FIVE_RADIANS = Math.toRadians(45);
        Point2D.Double expected = new Point2D.Double(Math.sin(FORTY_FIVE_RADIANS) * place.getWidth() / 2 + 15,
                Math.cos(FORTY_FIVE_RADIANS) * place.getWidth() / 2 + 15);

        Point2D.Double point = place.getArcEdgePoint(angle);
        assertEquals(expected, point);
    }

    @Test
    public void calculatesCorrectArcAttachmentPointsAsTarget() {
        Point source = new Point(0, 0);
        Point target = new Point(30, 30);
        double angle = getAngleBetweenObjects(source.x, source.y, target.x, target.y);

        place.setX(target.x);
        place.setY(target.y);

        double FOURTY_FIVE_RADIANS = Math.toRadians(45);
        Point2D.Double expected = new Point2D.Double(45 - Math.sin(FOURTY_FIVE_RADIANS) * place.getWidth() / 2,
                45 - Math.cos(FOURTY_FIVE_RADIANS) * place.getWidth() / 2);

        Point2D.Double point = place.getArcEdgePoint(Math.PI + angle);
        assertEquals(expected, point);
    }

    @Test
    public void addNewTokenSetsCountToOne() {
        place.incrementTokenCount("red");
        assertEquals(1, place.getTokenCount("red"));
    }

    @Test
    public void addExistingTokenIncrementsCount() {
        place.incrementTokenCount("red");

        place.incrementTokenCount("red");
        assertEquals(2, place.getTokenCount("red"));
    }

    @Test
    public void decrementExistingTokenDecreasesCount() {
        place.incrementTokenCount("red");

        place.decrementTokenCount("red");
        assertEquals(0, place.getTokenCount("red"));
    }

    @Test
    public void tokenCountIsZeroIfPlaceDoesNotContainToken() {
        assertEquals(0, place.getTokenCount("red"));
    }

    @Test
    public void throwsErrorIfSetTokenCountGreaterThanCapacity() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Cannot set token count that exceeds the capacity");
        place.setCapacity(1);
        place.setTokenCount("red", 2);
    }

    @Test
    public void throwsErrorIfIncrementTokenCountGreaterThanCapacity() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Cannot set token count that exceeds the capacity");
        place.setCapacity(1);

        place.incrementTokenCount("red");
        place.incrementTokenCount("red");
    }

    @Test
    public void capacityZeroMeansNoRestriction() {
        int capacity = 0;
        place.setCapacity(capacity);

        place.incrementTokenCount("red");
    }

    @Test
    public void setTokenCountsCannotExceedCapacity() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Count of tokens exceeds capacity!");
        place.setCapacity(1);

        Map<String, Integer> tokenCounts = new HashMap<>();
        tokenCounts.put("red", 10);

        place.setTokenCounts(tokenCounts);
    }

    @Test
    public void changingNumberOfTokensDoesNotTriggerExceedCapacityError() {
        int capacity = 1;
        place.setCapacity(capacity);

        place.incrementTokenCount("red");

        place.setTokenCount("red", 1);
    }

    @Test
    public void correctlyCountsNumberOfTokensStored() {
        int capacity = 20;
        place.setCapacity(capacity);

        int redTokenCount = 3;
        place.setTokenCount("red", 3);

        int blueTokenCount = 10;
        place.setTokenCount("blue", blueTokenCount);

        assertEquals(redTokenCount + blueTokenCount, place.getNumberOfTokensStored());
    }

    @Test
    public void notifiesObserverOnTokenChange() {
        PropertyChangeListener mockListener = mock(PropertyChangeListener.class);

        place.addPropertyChangeListener(mockListener);

        place.setTokenCount("Default", 7);
        verify(mockListener).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void notifiesObserverOnTokenMapChange() {
        Map<String, Integer> tokenCounts = new HashMap<>();
        PropertyChangeListener mockListener = mock(PropertyChangeListener.class);
        tokenCounts.put("Default", 7);
        place.addPropertyChangeListener(mockListener);

        place.setTokenCounts(tokenCounts);
        verify(mockListener).propertyChange(any(PropertyChangeEvent.class));
    }


}
