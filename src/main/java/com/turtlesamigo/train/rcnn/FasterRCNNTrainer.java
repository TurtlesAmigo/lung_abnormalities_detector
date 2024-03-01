package com.turtlesamigo.train.rcnn;

import com.turtlesamigo.model.AbnormalityClass;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;

public class FasterRCNNTrainer {
    private static final int HEIGHT = 3072;
    private static final int WIDTH = 3072;
    private static final int CHANNELS = 1;
    private static final int NUM_CLASSES = AbnormalityClass.values().length;

    public void getConfiguration() {
        ComputationGraphConfiguration.GraphBuilder graphBuilder = new NeuralNetConfiguration.Builder()
                .seed(123)
                .weightInit(WeightInit.RELU)
                .activation(Activation.RELU)
                .updater(new Adam())
                .graphBuilder()
                .addInputs("input")
                .setInputTypes(InputType.convolutional(HEIGHT, WIDTH, CHANNELS));
        MultiLayerNetwork network;
    }
}
