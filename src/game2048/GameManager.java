package game2048;

import javafx.animation.*;
import javafx.scene.Group;
import javafx.util.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by espen on 07/11/14.
 */
public class GameManager extends Group {

    public static final int FINAL_VALUE_TO_WIN = 2048;

    private static final Duration ANIMATION_EXISTING_TILE = Duration.millis(65);
    private static final Duration ANIMATION_NEWLY_ADDED_TILE = Duration.millis(125);
    private static final Duration ANIMATION_MERGED_TILE = Duration.millis(80);

    private volatile boolean movingTiles = false;
    private final List<Location> locations = new ArrayList<>();
    private final Map<Location, Tile> gameGrid;
    private final Set<Tile> mergedToBeRemoved = new HashSet<>();
    private final ParallelTransition parallelTransition = new ParallelTransition();

    private final Board board;
    private final GridOperator gridOperator;

    private boolean deadEnd;
    public int counter = 0;

    public GameManager(Map<Location, Tile> gameGrid, Board board, GridOperator gridOperator) {
        this.gameGrid = gameGrid;
        this.board = board;
        this.gridOperator = gridOperator;
        deadEnd = false;
    }

    public GameManager() {
        this(GridOperator.DEFAULT_GRID_SIZE);
    }

    public GameManager(int gridSize) {
        this.gameGrid = new HashMap<>();

        gridOperator=new GridOperator(gridSize);
        board = new Board(gridOperator);
        this.getChildren().add(board);

        board.clearGameProperty().addListener((ov, b, b1) -> {
            if (b1) {
                initializeGameGrid();
            }
        });
        board.resetGameProperty().addListener((ov, b, b1) -> {
            if (b1) {
                startGame();
            }
        });

        initializeGameGrid();
        Location.setLocations(locations);
        startGame();
    }
    private void initializeGameGrid() {
        gameGrid.clear();
        locations.clear();
        gridOperator.traverseGrid((x, y) -> {
            Location thisloc = new Location(x, y);
            locations.add(thisloc);
            gameGrid.put(thisloc, null);
            return 0;
        });
    }
    private void startGame() {
        Tile tile0 = Tile.newRandomTile();
        List<Location> randomLocs = new ArrayList<>(locations);
        Collections.shuffle(randomLocs);
        Iterator<Location> locs = randomLocs.stream().limit(2).iterator();
        tile0.setLocation(locs.next());

        Tile tile1 = null;
        if (new Random().nextFloat() <= 0.8) { // gives 80% chance to add a second tile
            tile1 = Tile.newRandomTile();
            if (tile1.getValue() == 4 && tile0.getValue() == 4) {
                tile1 = Tile.newTile(2);
            }
            tile1.setLocation(locs.next());
        }

        Arrays.asList(tile0, tile1).stream().filter(Objects::nonNull)
                .forEach(t -> gameGrid.put(t.getLocation(), t));

        redrawTilesInGameGrid();

        board.startGame();
    }

    private void redrawTilesInGameGrid() {
        gameGrid.values().stream().filter(Objects::nonNull).forEach(t->board.addTile(t));
    }

    public void setScale(double scale) {
        this.setScaleX(scale);
        this.setScaleY(scale);
    }

    public void pauseGame() {
    }

    public void quitGame() {
    }
    public Map<Location,Tile> abstractMove(Direction direction, Map<Location, Tile> grid) {

        return abstractMoveTiles(direction,deepCopyGrid(grid));
    }

    private Map<Location, Tile> abstractMoveTiles(Direction direction, Map<Location, Tile> grid) { //TODO: fucks up merging somehow

        gridOperator.sortGrid(direction);
        final int tilesWereMoved = gridOperator.traverseGrid((x, y) -> {
            Location thisloc = new Location(x, y);
            Location farthestLocation = findFarthestLocation(thisloc, direction); // farthest available location
            Optional<Tile> opTile = optionalTile(thisloc,grid);

            AtomicInteger result = new AtomicInteger();
            Location nextLocation = farthestLocation.offset(direction); // calculates to a possible merge
            optionalTile(nextLocation,grid).filter(t -> t.isMergeable(opTile) && !t.isMerged())
                    .ifPresent(t -> {
                        Tile tile = opTile.get();
                        t.merge(tile);
                        grid.put(nextLocation, t);
                        grid.replace(thisloc, null);

                        result.set(1);
                    });
            //if no merge and thisLoc has a value and it can slide further in the give direction
            if (result.get() == 0 && opTile.isPresent() && !farthestLocation.equals(thisloc)) {
                Tile tile = opTile.get();

                grid.put(farthestLocation, tile);
                grid.replace(thisloc, null);

                tile.setLocation(farthestLocation);

                result.set(1);
            }

            return result.get();
        });

            if ((!availableMove(grid) && mergeMovementsAvailable() == 0) || tilesWereMoved == 0 ) {
                // game is over if there are no more moves available
                return null;
            }/* else if (randomAvailableLocation != null && tilesWereMoved > 0) {
                Tile tile = Tile.newRandomTile();
                tile.setLocation(randomAvailableLocation);
                grid.put(tile.getLocation(), tile);
            }*/

            // reset merged after each movement
            grid.values().stream().filter(Objects::nonNull).forEach(Tile::clearMerge);
        return grid;
    }

    private Location findRandomAvailableLocation(Map<Location, Tile> grid) {
        //finds all tiles without a value
        List<Location> availableLocations = Location.getLocations().stream().filter(l -> grid.get(l) == null)
                .collect(Collectors.toList());

        if (availableLocations.isEmpty()) {
            return null;
        }

        //shuffles list of locations and picks a random location from it
        Collections.shuffle(availableLocations);
        Location randomLocation = availableLocations.get(new Random().nextInt(availableLocations.size()));
        return randomLocation;
    }

    private Optional<Tile> optionalTile(Location loc, Map<Location, Tile> grid) {
        return Optional.ofNullable(grid.get(loc));
    }

    public void move(Direction direction) {
        counter++;
        if (!board.isLayerOn().get()) {
            moveTiles(direction);
        }
        //System.out.println(" moved[" + counter + "]");
    }
    private void moveTiles(Direction direction) {
        synchronized (gameGrid) {
            if (movingTiles) {
                return;
            }
        }

        board.setPoints(0);

        gridOperator.sortGrid(direction);
        final int tilesWereMoved = gridOperator.traverseGrid((x, y) -> {
            Location thisloc = new Location(x, y);
            Location farthestLocation = findFarthestLocation(thisloc, direction); // farthest available location
            Optional<Tile> opTile = optionalTile(thisloc);

            AtomicInteger result = new AtomicInteger();
            Location nextLocation = farthestLocation.offset(direction); // calculates to a possible merge
            optionalTile(nextLocation).filter(t -> t.isMergeable(opTile) && !t.isMerged())
                    .ifPresent(t -> {
                        Tile tile = opTile.get();
                        t.merge(tile);
                        t.toFront();
                        gameGrid.put(nextLocation, t);
                        gameGrid.replace(thisloc, null);

                        parallelTransition.getChildren().add(animateExistingTile(tile, t.getLocation()));
                        parallelTransition.getChildren().add(animateMergedTile(t));
                        mergedToBeRemoved.add(tile);

                        board.addPoints(t.getValue());

                        if (t.getValue() == FINAL_VALUE_TO_WIN) {
                            board.setGameWin(true);
                        }
                        result.set(1);
                    });
            //if no merge and thisLoc has a value and it can slide further in the give direction
            if (result.get() == 0 && opTile.isPresent() && !farthestLocation.equals(thisloc)) {
                Tile tile = opTile.get();
                parallelTransition.getChildren().add(animateExistingTile(tile, farthestLocation));

                gameGrid.put(farthestLocation, tile);
                gameGrid.replace(thisloc, null);

                tile.setLocation(farthestLocation);

                result.set(1);
            }

            return result.get();
        });

        board.animateScore();

        //when all tils are moved this action is performed
        parallelTransition.setOnFinished(e -> {
            synchronized (gameGrid) {
                movingTiles = false;
            }

            board.getGridGroup().getChildren().removeAll(mergedToBeRemoved);

            Location randomAvailableLocation = findRandomAvailableLocation();
            if (randomAvailableLocation == null && mergeMovementsAvailable() == 0 ) {
                // game is over if there are no more moves available
                board.setGameOver(true);
            } else if (randomAvailableLocation != null && tilesWereMoved > 0) {
                addAndAnimateRandomTile(randomAvailableLocation);
            }

            mergedToBeRemoved.clear();

            // reset merged after each movement
            gameGrid.values().stream().filter(Objects::nonNull).forEach(Tile::clearMerge);
        });

        synchronized (gameGrid) {
            movingTiles = true;
        }

        parallelTransition.play();
        parallelTransition.getChildren().clear();
    }
    private Location findFarthestLocation(Location location, Direction direction) {
        Location farthest;

        do {
            farthest = location;
            location = farthest.offset(direction);
        } while (gridOperator.isValidLocation(location) && !optionalTile(location).isPresent());

        return farthest;
    }
    /**
     * optionalTile allows using tiles from the map at some location, whether they
     * are null or not
     * @param loc location of the tile
     * @return an Optional<Tile> containing null or a valid tile
     */
    private Optional<Tile> optionalTile(Location loc) {
        return Optional.ofNullable(gameGrid.get(loc));
    }

    private Location findRandomAvailableLocation() {
        //finds all tiles without a value
        List<Location> availableLocations = locations.stream().filter(l -> gameGrid.get(l) == null)
                .collect(Collectors.toList());

        if (availableLocations.isEmpty()) {
            return null;
        }

        //shuffles list of locations and picks a random location from it
        Collections.shuffle(availableLocations);
        Location randomLocation = availableLocations.get(new Random().nextInt(availableLocations.size()));
        return randomLocation;
    }


    private int mergeMovementsAvailable() {
        final AtomicInteger pairsOfMergeableTiles = new AtomicInteger();

        Stream.of(Direction.UP, Direction.LEFT).parallel().forEach(direction -> {
            gridOperator.traverseGrid((x, y) -> {
                Location thisloc = new Location(x, y);
                optionalTile(thisloc).ifPresent(t -> {
                    if (t.isMergeable(optionalTile(thisloc.offset(direction)))) {
                        pairsOfMergeableTiles.incrementAndGet();
                    }
                });
                return 0;
            });
        });
        return pairsOfMergeableTiles.get();
    }
    public boolean isGameOver(){
        return board.getGameOver();
    }
    public boolean isDeadEnd(){
        return deadEnd;
    }
    /*public GameManager getDeepCopy() {
        Map<Location,Tile> newGameGrid = deepCopyGrid();
        GameManager newManager = new GameManager(newGameGrid,this.board,this.gridOperator);




        return null;
    }*/

    public static Map<Location, Tile> deepCopyGrid(Map<Location, Tile> grid) {
        Map<Location,Tile> newGrid = new HashMap<>();
        grid.forEach((loc,tile) -> {
            Location newLoc = new Location(loc.getX(),loc.getY());
            if(tile == null)
                newGrid.put(newLoc,null);
            else{
                Tile newTile = Tile.newTile(tile.getValue());
                newTile.setLocation(newLoc);
                newGrid.put(newLoc, newTile);
            }
        });
        return newGrid;
    }
    public Map<Location, Tile> getGameGrid(){
        return gameGrid;
    }


    //Animations
    /**
     * Adds a tile of random value to a random location with a proper animation
     *
     * @param randomLocation
     */
    private void addAndAnimateRandomTile(Location randomLocation) {
        Tile tile = board.addRandomTile(randomLocation);
        gameGrid.put(tile.getLocation(), tile);

        animateNewlyAddedTile(tile).play();
    }

    /**
     * Animation that creates a fade in effect when a tile is added to the game
     * by increasing the tile scale from 0 to 100%
     * @param tile to be animated
     * @return a scale transition
     */
    private ScaleTransition animateNewlyAddedTile(Tile tile) {
        final ScaleTransition scaleTransition = new ScaleTransition(ANIMATION_NEWLY_ADDED_TILE, tile);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        scaleTransition.setInterpolator(Interpolator.EASE_OUT);
        scaleTransition.setOnFinished(e -> {
            // after last movement on full grid, check if there are movements available
            if (this.gameGrid.values().parallelStream().noneMatch(Objects::isNull) && mergeMovementsAvailable() == 0 ) {
                board.setGameOver(true);
            }
        });
        return scaleTransition;
    }

    /**
     * Animation that moves the tile from its previous location to a new location
     * @param tile to be animated
     * @param newLocation new location of the tile
     * @return a timeline
     */
    private Timeline animateExistingTile(Tile tile, Location newLocation) {
        Timeline timeline = new Timeline();
        KeyValue kvX = new KeyValue(tile.layoutXProperty(),
                newLocation.getLayoutX(Board.CELL_SIZE) - (tile.getMinHeight() / 2), Interpolator.EASE_OUT);
        KeyValue kvY = new KeyValue(tile.layoutYProperty(),
                newLocation.getLayoutY(Board.CELL_SIZE) - (tile.getMinHeight() / 2), Interpolator.EASE_OUT);

        KeyFrame kfX = new KeyFrame(ANIMATION_EXISTING_TILE, kvX);
        KeyFrame kfY = new KeyFrame(ANIMATION_EXISTING_TILE, kvY);

        timeline.getKeyFrames().add(kfX);
        timeline.getKeyFrames().add(kfY);

        return timeline;
    }

    /**
     * Animation that creates a pop effect when two tiles merge
     * by increasing the tile scale to 120% at the middle, and then going back to 100%
     * @param tile to be animated
     * @return a sequential transition
     */
    private SequentialTransition animateMergedTile(Tile tile) {
        final ScaleTransition scale0 = new ScaleTransition(ANIMATION_MERGED_TILE, tile);
        scale0.setToX(1.2);
        scale0.setToY(1.2);
        scale0.setInterpolator(Interpolator.EASE_IN);

        final ScaleTransition scale1 = new ScaleTransition(ANIMATION_MERGED_TILE, tile);
        scale1.setToX(1.0);
        scale1.setToY(1.0);
        scale1.setInterpolator(Interpolator.EASE_OUT);

        return new SequentialTransition(scale0, scale1);
    }

    public static boolean availableMove(Map<Location, Tile> grid) {

        return !grid.values().stream().filter(tile -> tile != null).collect(Collectors.toList()).isEmpty();
        /*for (Tile tile: grid.values()){
            if(tile != null)
                return true;
        }
        return false;*/
    }

    public static List<GridOutcome> getAllOutcomes(Map<Location, Tile> grid) {
        List<Location> availableLocations = Location.getLocations().stream().filter(l -> grid.get(l) == null)
                .collect(Collectors.toList());
        List<GridOutcome> outcomes = new ArrayList<>();

        for (Location location: availableLocations){
            outcomes.add(getGridWithNewTile(grid,location,2,0.9));
            outcomes.add(getGridWithNewTile(grid,location,4,0.1));
        }

        return outcomes;
    }

    private static GridOutcome getGridWithNewTile(Map<Location, Tile> grid, Location location, int value, double probability) {
        Map<Location,Tile> newGrid = deepCopyGrid(grid);
        Tile tile = Tile.newTile(value);
        Location newLoc = new Location(location.getX(),location.getY());
        tile.setLocation(newLoc);
        newGrid.put(newLoc,tile);
        GridOutcome outcome = new GridOutcome();
        outcome.setGrid(newGrid);
        outcome.setProbability(probability);

        return outcome;
    }

}
