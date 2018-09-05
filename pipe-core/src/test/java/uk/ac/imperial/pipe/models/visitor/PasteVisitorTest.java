package uk.ac.imperial.pipe.models.visitor;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.*;
import uk.ac.imperial.pipe.naming.MultipleNamer;
import uk.ac.imperial.pipe.visitor.PasteVisitor;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PasteVisitorTest {
    private final static String PLACE_NAME = "MOCK_PLACE_COPIED";

    private final static String TRANSITION_NAME = "MOCK_TRANSITION_COPIED";

    PasteVisitor visitor;

    @Mock
    PetriNet petriNet;

    Collection<PetriNetComponent> pasteComponents;

    @Mock
    MultipleNamer mockNamer;

    @Before
    public void setUp() {
        pasteComponents = new LinkedList<>();
        when(mockNamer.getPlaceName()).thenReturn(PLACE_NAME);
        when(mockNamer.getTransitionName()).thenReturn(TRANSITION_NAME);
    }

    @Test
    public void pastingPlace() throws PetriNetComponentException {
        Place place = new DiscretePlace("id", "name");
        place.setCapacity(10);
        pasteComponents.add(place);
        visitor = new PasteVisitor(petriNet, pasteComponents, mockNamer);

        doPaste();

        verify(petriNet).addPlace(argThat(matchesThisPlaceWithCopiedNameAndId(place)));
    }

    private void doPaste() throws PetriNetComponentException {
        for (PetriNetComponent component : pasteComponents) {
            component.accept(visitor);
        }
    }

    private Matcher<Place> matchesThisPlaceWithCopiedNameAndId(Place place) {
        return new CopiedPlace(place, PLACE_NAME);
    }

    @Test
    public void pastingPlaceWithOffset() throws PetriNetComponentException {
        Place place = new DiscretePlace("id", "name");
        place.setCapacity(10);
        pasteComponents.add(place);
        Point offset = new Point(40, 20);
        visitor = new PasteVisitor(petriNet, pasteComponents, mockNamer, offset.x, offset.y);

        doPaste();

        verify(petriNet).addPlace(argThat(matchesThisPlaceWithCopiedNameAndIdAndOffset(place, offset)));
    }

    private Matcher<Place> matchesThisPlaceWithCopiedNameAndIdAndOffset(Place place, Point2D offset) {
        return new CopiedPlace(place, offset, mockNamer.getPlaceName());
    }

    @Test
    public void pastingTransition() throws PetriNetComponentException {
        Transition transition = new DiscreteTransition("id", "name");
        transition.setAngle(45);
        transition.setPriority(10);
        pasteComponents.add(transition);
        visitor = new PasteVisitor(petriNet, pasteComponents, mockNamer);

        doPaste();

        verify(petriNet).addTransition(argThat(matchesThisTransitionWithCopiedNameAndId(transition)));
    }

    private Matcher<Transition> matchesThisTransitionWithCopiedNameAndId(Transition transition) {
        return new CopiedTransition(transition, TRANSITION_NAME);
    }

    @Test
    public void pastingTransitionWithOffset() throws PetriNetComponentException {
        Transition transition = new DiscreteTransition("id", "name");
        transition.setAngle(45);
        transition.setPriority(10);
        pasteComponents.add(transition);
        Point offset = new Point(40, 20);
        visitor = new PasteVisitor(petriNet, pasteComponents, mockNamer, offset.x, offset.y);

        doPaste();

        verify(petriNet).addTransition(argThat(matchesThisTransitionWithCopiedNameAndIdAndOffset(transition, offset)));
    }

    private Matcher<Transition> matchesThisTransitionWithCopiedNameAndIdAndOffset(Transition transition,
                                                                                  Point2D offset) {
        return new CopiedTransition(transition, offset, TRANSITION_NAME);
    }

    @Test
    public void pastingArcTransitionAndPlaceInSelected() throws PetriNetComponentException {
        Place place = new DiscretePlace("id", "name");
        Transition transition = new DiscreteTransition("id", "name");
        pasteComponents.add(place);
        pasteComponents.add(transition);

        Map<String, String> weights = new HashMap<>();
        InboundArc arc = new InboundNormalArc(place, transition, weights);
        pasteComponents.add(arc);
        visitor = new PasteVisitor(petriNet, pasteComponents, mockNamer);

        doPaste();

        verify(petriNet).addArc(argThat(hasCopiedIdAndNameAndBothComponentsAreCopied(arc)));
    }

    private Matcher<InboundArc> hasCopiedIdAndNameAndBothComponentsAreCopied(InboundArc arc) {
        return new CopiedArc(arc, PLACE_NAME, TRANSITION_NAME);
    }

    @Test
    public void pastingArcSourceInSelected() throws PetriNetComponentException {
        Place place = new DiscretePlace("id", "name");
        pasteComponents.add(place);

        Transition transition = new DiscreteTransition("id", "name");
        Map<String, String> weights = new HashMap<>();
        InboundArc arc = new InboundNormalArc(place, transition, weights);
        pasteComponents.add(arc);
        visitor = new PasteVisitor(petriNet, pasteComponents, mockNamer);

        doPaste();

        verify(petriNet).addArc(argThat(hasCopiedIdAndNameAndSourceCopied(arc)));
    }

    private Matcher<InboundArc> hasCopiedIdAndNameAndSourceCopied(
            Arc<? extends Connectable, ? extends Connectable> arc) {
        return new CopiedArc<>(arc, PLACE_NAME, arc.getTarget().getName());
    }

    @Test
    public void pastingArcKeepsIntermediatePoints() throws PetriNetComponentException {

        Place place = new DiscretePlace("id", "name");
        pasteComponents.add(place);

        Transition transition = new DiscreteTransition("id", "name");
        Map<String, String> weights = new HashMap<>();
        InboundArc arc = new InboundNormalArc(place, transition, weights);
        ArcPoint arcPoint = new ArcPoint(new Point2D.Double(200, 100), true);
        arc.addIntermediatePoint(arcPoint);

        pasteComponents.add(arc);
        visitor = new PasteVisitor(petriNet, pasteComponents, mockNamer);

        doPaste();

        verify(petriNet).addArc(argThat(hasCopiedIntermediatePoints(arc)));
    }

    /**
     * Ensures arc points are equal but not the same object
     *
     * @param arc
     * @return
     */
    private Matcher<InboundArc> hasCopiedIntermediatePoints(final Arc<Place, Transition> arc) {
        return new ArgumentMatcher<InboundArc>() {
            @Override
            public boolean matches(Object argument) {

                InboundArc otherArc = (InboundArc) argument;
                List<ArcPoint> arcPoints = arc.getArcPoints();
                List<ArcPoint> otherPoints = otherArc.getArcPoints();
                if (arcPoints.size() != otherPoints.size()) {
                    return false;
                }
                for (int index = 1; index < arcPoints.size() - 1; index++) {
                    ArcPoint arcPoint = arcPoints.get(index);
                    ArcPoint otherArcPoint = otherPoints.get(index);
                    if (arcPoint == otherArcPoint || !arcPoint.equals(otherArcPoint)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    @Test
    public void pastingArcTargetInSelected() throws PetriNetComponentException {
        Place place = new DiscretePlace("id", "name");
        Transition transition = new DiscreteTransition("id", "name");
        pasteComponents.add(transition);

        Map<String, String> weights = new HashMap<>();
        InboundArc arc = new InboundNormalArc(place, transition, weights);
        pasteComponents.add(arc);
        visitor = new PasteVisitor(petriNet, pasteComponents, mockNamer);

        doPaste();

        verify(petriNet).addArc(argThat(hasCopiedIdAndNameAndTargetCopied(arc)));
    }

    private Matcher<InboundArc> hasCopiedIdAndNameAndTargetCopied(
            Arc<? extends Connectable, ? extends Connectable> arc) {
        return new CopiedArc<>(arc, arc.getSource().getName(), TRANSITION_NAME);
    }

    /**
     * Makes sure that the argument in matches is identical to the specified place
     * except name and id match id
     */
    private static class CopiedPlace extends ArgumentMatcher<Place> {

        /**
         * Place that should be copied
         */
        private final Place place;

        private final String id;

        private final Point2D offset;

        private CopiedPlace(Place place, String id) {
            this(place, new Point2D.Double(0, 0), id);
        }

        public CopiedPlace(Place place, Point2D offset, String id) {
            this.place = place;
            this.offset = offset;
            this.id = id;
        }

        @Override
        public boolean matches(Object argument) {
            Place otherPlace = (Place) argument;
            return (otherPlace.getId().equals(id) && otherPlace.getName().equals(id) && otherPlace.getX() == (
                    place.getX() + offset.getX()) &&
                    otherPlace.getY() == (place.getY() + offset.getY()) &&
                    otherPlace.getNameXOffset() == place.getNameXOffset() &&
                    otherPlace.getNameYOffset() == place.getNameYOffset() &&
                    otherPlace.getCapacity() == place.getCapacity() &&
                    otherPlace.getTokenCounts().equals(place.getTokenCounts()) &&
                    otherPlace.getMarkingXOffset() == place.getMarkingXOffset() &&
                    otherPlace.getMarkingYOffset() == place.getMarkingYOffset());
        }
    }

    /**
     * Makes sure that the argument in matches is identical to the specified transition
     * except name and id have "_copied" appended to them
     */
    private static class CopiedTransition extends ArgumentMatcher<Transition> {

        private final Point2D offset;

        private final String id;

        private Transition transition;

        private CopiedTransition(Transition transition, String id) {
            this(transition, new Point2D.Double(0, 0), id);
        }


        public CopiedTransition(Transition transition, Point2D offset, String id) {
            this.transition = transition;
            this.offset = offset;
            this.id = id;
        }


        @Override
        public boolean matches(Object argument) {
            Transition otherTransition = (Transition) argument;
            return (otherTransition.getId().equals(id) && otherTransition.getName().equals(id)
                    && otherTransition.getX() == (transition.getX() + offset.getX()) &&
                    otherTransition.getY() == (transition.getY() + offset.getY()) &&
                    otherTransition.getNameXOffset() == transition.getNameXOffset() &&
                    otherTransition.getNameYOffset() == transition.getNameYOffset() &&
                    otherTransition.getRateExpr().equals(transition.getRateExpr()) &&
                    otherTransition.isInfiniteServer() == transition.isInfiniteServer() &&
                    otherTransition.getAngle() == transition.getAngle() &&
                    otherTransition.isTimed() == transition.isTimed() &&
                    otherTransition.getPriority() == transition.getPriority());
        }
    }

    private static class CopiedArc<T> extends ArgumentMatcher<T> {

        private final Arc<? extends Connectable, ? extends Connectable> arc;

        private final String sourceName;

        private final String targetName;

        public CopiedArc(Arc<? extends Connectable, ? extends Connectable> arc, String sourceName, String targetName) {

            this.arc = arc;
            this.sourceName = sourceName;
            this.targetName = targetName;
        }

        @Override
        public boolean matches(Object argument) {
            Arc<? extends Connectable, ? extends Connectable> otherArc =
                    (Arc<? extends Connectable, ? extends Connectable>) argument;


            return (otherArc.getSource().getName().equals(sourceName) && otherArc.getTarget().getName().equals(
                    targetName));
        }
    }

}
