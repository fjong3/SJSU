#include <iostream>
#include <iomanip>
#include <cmath>
#include <boost/chrono.hpp>
#include "omp.h"

using namespace boost::chrono;

/**
 * @brief basic boost chrono
 *
 * To build:
 * g++ -I /usr/local/gcc/boost_1_60_0/include -L /usr/local/gcc/boost_1_60_0/lib -lboost_chrono -lboost_system -std=c++14 round-ex.cpp -o round-ex
 *
 *      Author: gash
 */
int main(int argc, char **argv) {

    const auto A = 2048;
    const auto B = 1024;

    auto ref = 0.0;
    for ( int a = 1 ; a < A ; a++ ) { 
        for ( int b = 1 ; b < B ; b++ ) { 
            ref += cos(a) / sqrt(b);
        }
    } 

    auto dt_s = high_resolution_clock::now();

    auto val = 0.0;
    #pragma omp parallel 
    {
        #pragma omp for schedule(guided) reduction(+:val)
        for ( int a = 1 ; a < A ; a++ ) {
            for ( int b = 1 ; b < B ; b++ ) {
                val += cos(a) / sqrt(b);
            }
        } 
    }

    auto dt = duration_cast<nanoseconds> (high_resolution_clock::now() - dt_s); 
    std::cout << "\ndt = " << dt.count() << " ns" << "\n";
    std::cout << "agreement: " << std::setprecision(18) << (ref - val) << "\n";

}
