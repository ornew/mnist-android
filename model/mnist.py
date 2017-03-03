#!/usr/bin/env python

import os
import time
import argparse

import tensorflow as tf
from tensorflow.examples.tutorials.mnist import input_data
from tensorflow.python.tools.freeze_graph import freeze_graph

def build_inference(x, keep_prob=None):
    def weight(shape):
        initial = tf.truncated_normal(shape, stddev=0.1)
        return tf.Variable(initial, name='weight')
    def bias(shape):
        initial = tf.constant(0.1, shape=shape)
        return tf.Variable(initial, name='bias')
    def convolution(x, W):
        return tf.nn.conv2d(x, W, strides=[1, 1, 1, 1], padding='SAME', name='convolutional')
    def pooling(x):
        return tf.nn.max_pool(x, ksize=[1, 2, 2, 1], strides=[1, 2, 2, 1], padding='SAME', name='pooling')

    x_image = tf.reshape(x, [-1,28,28,1])
    with tf.name_scope('hidden_1'):
        W1 = weight([5, 5, 1, 32])
        b1 = bias([32])
        C1 = tf.nn.relu(convolution(x_image, W1) + b1)
        h1 = pooling(C1)
    with tf.name_scope('hidden_2'):
        W2 = weight([5, 5, 32, 64])
        b2 = bias([64])
        C2 = tf.nn.relu(convolution(h1, W2) + b2)
        h2 = pooling(C2)
    with tf.name_scope('fully_connect'):
        Wfc = weight([7 * 7 * 64, 1024])
        bfc = bias([1024])
        h2_flat = tf.reshape(h2, [-1, 7 * 7 * 64])
        hfc = tf.nn.relu(tf.matmul(h2_flat, Wfc) + bfc)
    with tf.name_scope('dropout'):
        if(keep_prob != None):
            fc = tf.nn.dropout(hfc, keep_prob)
        else:
            fc = hfc
    with tf.name_scope('readout'):
        Wr = weight([1024, 10])
        br = bias([10])
        y = tf.matmul(fc, Wr) + br
        return tf.identity(y, name='y')

def build_loss(labels, logits):
    with tf.name_scope('loss'):
        cross_entropy = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(labels=labels, logits=logits))
        tf.summary.scalar('cross_entropy', cross_entropy)
    return cross_entropy

def build_train(loss, learning_rate):
    with tf.name_scope('optimizer'):
        training = tf.train.AdamOptimizer(learning_rate).minimize(loss)
    return training

def train(FLAGS, mnist_data):
    with tf.Graph().as_default():
        # Placeholders
        x = tf.placeholder(tf.float32, shape=[None, 784], name='x')
        y = tf.placeholder(tf.float32, shape=[None, 10], name='label')
        keep_prob     = tf.placeholder(tf.float32, name='keep_prob')
        learning_rate = tf.placeholder(tf.float32, name='learning_rate')

        # Build graph.
        inference = build_inference(x, keep_prob)
        loss      = build_loss(y, inference)
        train     = build_train(loss, learning_rate)
        merge     = tf.summary.merge_all()

        print('Checkpoints directory: %s' % FLAGS.ckpt_dir)
        if tf.gfile.Exists(FLAGS.ckpt_dir):
            print('Cleaning checkpoints...')
            tf.gfile.DeleteRecursively(FLAGS.ckpt_dir)
        tf.gfile.MakeDirs(FLAGS.ckpt_dir)
        saver = tf.train.Saver(max_to_keep=1)

        # Initialize session.
        session = tf.Session()
        session.run(tf.global_variables_initializer())
        writer = tf.summary.FileWriter(FLAGS.log_dir, session.graph)

        print('Start training...')
        start = time.time()
        for step in range(1,20000+1):
            batch = mnist_data.train.next_batch(50)
            _, summary = session.run([train, merge], feed_dict={
                x: batch[0],
                y: batch[1],
                keep_prob: 0.5,
                learning_rate: 1e-4})
            writer.add_summary(summary, global_step=step)
        elapsed_time = time.time() - start
        print('done!')
        print('Total time: %f [sec]' % elapsed_time)
        ckpt = saver.save(session, os.path.join(FLAGS.ckpt_dir, 'ckpt'), global_step=step)
        print('Save checkpoint: %s' % ckpt)

        # Write graph.
        tf.train.write_graph(session.graph.as_graph_def(), FLAGS.model_dir, FLAGS.model_name, as_text=False)
        tf.train.write_graph(session.graph.as_graph_def(), FLAGS.model_dir, FLAGS.model_name + '.txt', as_text=True)

        # Freeze graph.
        freeze_graph(
            input_graph=os.path.join(FLAGS.model_dir, FLAGS.model_name + '.txt'),
            input_saver='',
            input_binary=False,
            input_checkpoint=ckpt,
            output_node_names='readout/y',
            restore_op_name='save/restore_all',
            filename_tensor_name='save/Const:0',
            output_graph=os.path.join(FLAGS.model_dir, '%s.frozen.pb' % FLAGS.model_name),
            clear_devices=False,
            initializer_nodes='')

if __name__ == '__main__':
    pwd = os.path.abspath(os.path.dirname(__file__))
    parser = argparse.ArgumentParser()
    parser.add_argument(
        '--model_dir',
        type=str,
        default=os.path.join(pwd, 'dist'),
        help='Model directory.')
    parser.add_argument(
        '--model_name',
        type=str,
        default='mnist',
        help='Model name.')
    parser.add_argument(
        '--ckpt_dir',
        type=str,
        default=os.path.join(pwd, 'ckpt'),
        help='Checkpoints directory.')
    parser.add_argument(
        '--log_dir',
        type=str,
        default=os.path.join(pwd, 'log'),
        help='The training log outputed directory.')
    parser.add_argument(
        '--data_dir',
        type=str,
        default=os.path.join(pwd, 'MNIST_data'),
        help='MNIST data directory.')
    FLAGS, unparsed = parser.parse_known_args()
    mnist_data = input_data.read_data_sets(FLAGS.data_dir, one_hot=True)
    train(FLAGS, mnist_data=mnist_data)
    print('done')
