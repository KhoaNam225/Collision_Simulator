public class Simulator {
    private static final double HZ = 0.5;

    private Particle[] particles;
    private MinPQ<Event> pq;
    private double time;
    private int frameWidth = Draw.DEFAULT_SIZE, frameHeight = Draw.DEFAULT_SIZE;

    public Simulator(int pNum) {
        particles = new Particle[pNum];
        for (int i = 0; i < pNum; i++) {
            particles[i] = new Particle(frameWidth, frameHeight);
        }

        time = 0.0;
        pq = new MinPQ<>();
    }

    public void simulate() {
        for (int i = 0; i < particles.length; i++) 
            predictCollission(particles[i]);
        pq.insert(new Event(null, null, 0));

        while (!pq.isEmpty()) {
            Event event = pq.remove();
            if (event.isValid()) {
                Particle a = event.a;
                Particle b = event.b;
                double dt = event.time - time;

                for (int i = 0; i < particles.length; i++) 
                    particles[i].move(dt);

                time = event.time;
                if (a != null && b != null)
                    a.bounceOff(b);
                else if (a == null && b != null)
                    b.bounceOffHorizontalWall();
                else if (a != null && b == null)
                    a.bounceOffVerticalWall();
                else
                    redraw();

                predictCollission(a);
                predictCollission(b);
            }
        }
    } 

    private void predictCollission(Particle a) {
        if (a != null) {
            for (int i = 0; i < particles.length; i++) {
                pq.insert(new Event(a, particles[i], time + a.timeToHit(particles[i])));
            }

            pq.insert(new Event(a, null, time + a.timeToHitVerticalWall(frameWidth)));
            pq.insert(new Event(null, a, time + a.timeToHitHorizontalWall(frameHeight)));
        }
    }

    private void redraw() {
        Draw.clear();
        for (int i = 0;i < particles.length; i++) 
            particles[i].draw();

        Draw.show();
        Draw.pause(25);
        pq.insert(new Event(null, null, time + 1 / HZ));
    }

    private class Event implements Comparable<Event> {
        private Particle a, b;
        private int countA, countB;
        private double time;

        public Event(Particle a, Particle b, double time) {
            this.a = a;
            this.b = b;
            this.time = time;
            this.countA = a == null ? -1 : a.count();
            this.countB = b == null ? -1 : b.count();
        }

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

    public static void main(String[] args) {
        Draw.enableDoubleBuffering();
        Simulator simulator = new Simulator(10);
        simulator.simulate();
    }
}