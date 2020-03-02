/**
 * Author:      Khoa Nam Pham
 * Date modified:   02/03/2020
 * Purpose:     The main Simulator class that control the simulation.
 */

public class Simulator {
    private static final double HZ = 0.5;  // The number of frame drawn per clock tick

    private Particle[] particles;  // All the particles in the simulation
    private MinPQ<Event> pq;  // The queue to maintain the next coming event
    private double time;  // The elapsed time the simulation had happend
    private int frameWidth = Draw.DEFAULT_SIZE, frameHeight = Draw.DEFAULT_SIZE;  // Canvas size

    /**
     * Create a simulator with the given number of particles.
     * All the particles in the simulator will be generated randomly.
     */
    public Simulator(int pNum) {
        particles = new Particle[pNum];
        for (int i = 0; i < pNum; i++) {
            particles[i] = new Particle(frameWidth, frameHeight);
        }

        time = 0.0;
        pq = new MinPQ<>();
    }

    /**
     * Starts the simulation with the given time limit
     * @param limit The time limit for the simulation, simulation will stop after this amount of time.
     */
    public void simulate(double limit) {
        // Pre-fill the queue with all initial events
        for (int i = 0; i < particles.length; i++) 
            predictCollission(particles[i], limit);
        pq.insert(new Event(null, null, 0));  // Draw the particles first

        // While there is still an event in the queue
        while (!pq.isEmpty()) {
            Event event = pq.remove();
        
            if (event.isValid()) {
                Particle a = event.a;
                Particle b = event.b;
                double dt = event.time - time;

                // Move all the particles toward the moment when the event occurs
                for (int i = 0; i < particles.length; i++) 
                    particles[i].move(dt);

                time = event.time;

                // Apply approriate action according to the event
                if (a != null && b != null)
                    a.bounceOff(b);
                else if (a == null && b != null)
                    b.bounceOffHorizontalWall();
                else if (a != null && b == null)
                    a.bounceOffVerticalWall();
                else
                    redraw(limit);

                // Predict collsion for the participating particles
                predictCollission(a, limit); 
                predictCollission(b, limit);
            }
        }
    } 

    /**
     * Predict all the possible collision involving the given particle
     * @param a The particle that needs to be assessing
     * @param limit The simulation time limit
     */
    private void predictCollission(Particle a, double limit) {
        if (a != null) {
            // Particle-Particle prediction
            for (int i = 0; i < particles.length; i++) {
                double dt = a.timeToHit(particles[i]);
                if (dt + time < limit)
                    pq.insert(new Event(a, particles[i], time + dt));
            }

            // Particle-Wall prediction
            double dtX = a.timeToHitVerticalWall(frameWidth);
            double dtY = a.timeToHitHorizontalWall(frameHeight);
            
            if (time + dtX < limit)
                pq.insert(new Event(a, null, time + dtX));
            
                if (time + dtY < limit)
                pq.insert(new Event(null, a, time + dtY));
        }
    }

    /**
     * Draw all the particles
     * @param limit The simulation time limit
     */
    private void redraw(double limit) {
        Draw.clear();
        for (int i = 0;i < particles.length; i++) 
            particles[i].draw();

        Draw.show();
        Draw.pause(25);
        
        if (time + 1 / HZ < limit)
            pq.insert(new Event(null, null, time + 1 / HZ));
    }

    /**
     * This class represent an event that can happen in the simulation
     * Each event will have:
     * a, b: 2 particles involving in the event
     * time: the time when the event happens
     * countA, countB: The count number to determine if there is no event intervens the current event
     * 
     * The particles will define the type of the event, there are 4 types of event during the simulation:
     * a = null, b = null: redraw event
     * a = null, b != null: particle-horizontal wall event
     * a != null, b = null: particle-vertical wall event
     * a != null, b != null: particle-particle event
     */
    private class Event implements Comparable<Event> {
        private Particle a, b;
        private int countA, countB;
        private double time;

        /**
         * Create a new event with the given particles and time
         * @param a particle A
         * @param b particle B
         * @param time the time when the event happen
         */
        public Event(Particle a, Particle b, double time) {
            this.a = a;
            this.b = b;
            this.time = time;
            this.countA = a == null ? -1 : a.count();
            this.countB = b == null ? -1 : b.count();
        }

        /**
         * Checks if an event is valid, it is valid if no other events intervens it.
         * @return true if valid or false otherwise
         */
        public boolean isValid() {
            if (a != null && countA != a.count()) return false;
            if (b != null && countB != b.count()) return false;

            return true;
        }

        @Override
        public int compareTo(Event other) {
            return Double.compare(this.time, other.time);
        }
    }

    // Test client
    public static void main(String[] args) {
        if (args.length < 2)
            System.out.println("Please specify the number of particles and the simulation time");
        else {
            int pNum = Integer.parseInt(args[0]);
            double limit = Double.parseDouble(args[1]);
            
            Draw.enableDoubleBuffering();
            
            Simulator simulator = new Simulator(pNum);
            simulator.simulate(limit);
        }
    }
}