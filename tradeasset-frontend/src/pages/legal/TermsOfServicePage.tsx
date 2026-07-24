/**
 * Terms of Service Page
 * Legal terms and conditions for using TradeSphere
 */

import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowLeft, FileText } from 'lucide-react';
import { Card, CardBody, Button } from '../../components/ui';
import { ROUTES } from '../../config/constants';
import './LegalPages.css';

export const TermsOfServicePage: React.FC = () => {
  return (
    <div className="legal-page-container">
      <div className="legal-page-wrapper">
        <div className="legal-page-header">
          <Link to={ROUTES.LOGIN} className="legal-back-link">
            <ArrowLeft size={20} />
            <span>Back to Login</span>
          </Link>
        </div>

        <Card variant="elevated" padding="lg" className="legal-card">
          <div className="legal-title">
            <FileText size={32} />
            <h1>Terms of Service</h1>
          </div>
          <div className="legal-subtitle">Last Updated: July 21, 2026</div>
          
          <CardBody>
            <div className="legal-content">
              <section className="legal-section">
                <h2>1. Acceptance of Terms</h2>
                <p>
                  By accessing and using TradeSphere ("Service"), you accept and agree to be bound by the terms
                  and provision of this agreement. If you do not agree to abide by the above, please do not use
                  this service.
                </p>
              </section>

              <section className="legal-section">
                <h2>2. Description of Service</h2>
                <p>
                  TradeSphere provides a comprehensive trade asset management platform for financial institutions,
                  enabling the management of letters of credit, bank guarantees, collections, supply chain finance,
                  and related trade finance instruments.
                </p>
              </section>

              <section className="legal-section">
                <h2>3. User Accounts</h2>
                <h3>3.1 Account Registration</h3>
                <p>
                  To use our Service, you must register for an account. You agree to provide accurate, current,
                  and complete information during the registration process and to update such information to keep
                  it accurate, current, and complete.
                </p>
                <h3>3.2 Account Security</h3>
                <p>
                  You are responsible for safeguarding the password that you use to access the Service and for
                  any activities or actions under your password. You agree not to disclose your password to any
                  third party. You must notify us immediately upon becoming aware of any breach of security or
                  unauthorized use of your account.
                </p>
                <h3>3.3 Account Termination</h3>
                <p>
                  We reserve the right to suspend or terminate your account if any information provided during
                  the registration process or thereafter proves to be inaccurate, false, or misleading.
                </p>
              </section>

              <section className="legal-section">
                <h2>4. User Obligations</h2>
                <p>You agree to use the Service only for lawful purposes and in accordance with these Terms. You agree not to:</p>
                <ul>
                  <li>Use the Service in any way that violates any applicable national or international law or regulation</li>
                  <li>Transmit any material that is defamatory, offensive, or otherwise objectionable</li>
                  <li>Attempt to gain unauthorized access to any portion of the Service or any other systems or networks</li>
                  <li>Interfere with or disrupt the Service or servers or networks connected to the Service</li>
                  <li>Use any automated means to access the Service for any purpose without our express written permission</li>
                  <li>Impersonate or attempt to impersonate the Company, a Company employee, another user, or any other person or entity</li>
                </ul>
              </section>

              <section className="legal-section">
                <h2>5. Intellectual Property Rights</h2>
                <p>
                  The Service and its original content, features, and functionality are and will remain the
                  exclusive property of TradeSphere Inc. and its licensors. The Service is protected by copyright,
                  trademark, and other laws of both the United States and foreign countries. Our trademarks and
                  trade dress may not be used in connection with any product or service without the prior written
                  consent of TradeSphere Inc.
                </p>
              </section>

              <section className="legal-section">
                <h2>6. Data and Privacy</h2>
                <p>
                  Your use of the Service is also governed by our Privacy Policy. Please review our Privacy Policy,
                  which also governs the Service and informs users of our data collection practices.
                </p>
              </section>

              <section className="legal-section">
                <h2>7. Financial Transactions</h2>
                <p>
                  The Service facilitates the management of trade finance instruments. Users acknowledge that
                  all financial transactions are subject to applicable banking regulations, international trade
                  laws, and compliance requirements. TradeSphere Inc. is not responsible for the execution of
                  underlying financial transactions.
                </p>
              </section>

              <section className="legal-section">
                <h2>8. Limitation of Liability</h2>
                <p>
                  In no event shall TradeSphere Inc., nor its directors, employees, partners, agents, suppliers,
                  or affiliates, be liable for any indirect, incidental, special, consequential, or punitive damages,
                  including without limitation, loss of profits, data, use, goodwill, or other intangible losses,
                  resulting from your access to or use of or inability to access or use the Service.
                </p>
              </section>

              <section className="legal-section">
                <h2>9. Disclaimer of Warranties</h2>
                <p>
                  The Service is provided on an "AS IS" and "AS AVAILABLE" basis. The Service is provided without
                  warranties of any kind, whether express or implied, including, but not limited to, implied
                  warranties of merchantability, fitness for a particular purpose, non-infringement, or course
                  of performance.
                </p>
              </section>

              <section className="legal-section">
                <h2>10. Indemnification</h2>
                <p>
                  You agree to defend, indemnify, and hold harmless TradeSphere Inc. and its licensee and licensors,
                  and their employees, contractors, agents, officers, and directors, from and against any and all
                  claims, damages, obligations, losses, liabilities, costs or debt, and expenses arising from your
                  use of and access to the Service, or from your violation of these Terms.
                </p>
              </section>

              <section className="legal-section">
                <h2>11. Changes to Terms</h2>
                <p>
                  We reserve the right, at our sole discretion, to modify or replace these Terms at any time.
                  If a revision is material, we will try to provide at least 30 days' notice prior to any new
                  terms taking effect. What constitutes a material change will be determined at our sole discretion.
                </p>
              </section>

              <section className="legal-section">
                <h2>12. Governing Law</h2>
                <p>
                  These Terms shall be governed and construed in accordance with the laws of the United States,
                  without regard to its conflict of law provisions. Our failure to enforce any right or provision
                  of these Terms will not be considered a waiver of those rights.
                </p>
              </section>

              <section className="legal-section">
                <h2>13. Contact Information</h2>
                <p>
                  If you have any questions about these Terms, please contact us at:
                </p>
                <div className="contact-info">
                  <p><strong>Email:</strong> legal@tradesphere.com</p>
                  <p><strong>Address:</strong> TradeSphere Inc., 123 Finance Street, New York, NY 10001, USA</p>
                  <p><strong>Phone:</strong> +1 (555) 123-4567</p>
                </div>
              </section>
            </div>

            <div className="legal-actions">
              <Link to={ROUTES.LOGIN}>
                <Button variant="primary" size="lg">
                  I Understand
                </Button>
              </Link>
            </div>
          </CardBody>
        </Card>
      </div>
    </div>
  );
};

