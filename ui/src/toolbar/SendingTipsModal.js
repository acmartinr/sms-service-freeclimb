import React, {Component} from 'react';

import './MessagesModal.css';
import '../common/Common.css';

import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';

class SendingTipsLogic extends Component {

  render() {
    return (
      <Dialog
        open
        onClose={this.props.handleClose}
        aria-labelledby="form-dialog-title"
        className="custom-messages-dialog">

        <DialogTitle
          className="padding-bottom-0"
          id="form-dialog-title">Sending Tips</DialogTitle>

        <DialogContent>
          <p align="center" STYLE="margin-bottom: 0in; line-height: 100%; font-size: 1.25rem;">Caller
          IDs</p>
          <ul>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">A caller ID
          	phone number can send 150 message per day</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">The Caller ID
          	tab is the 4<sup>th</sup> tab on the left of the screen</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">For best
          	delivery, buy at least 20 phone numbers</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Caller IDs Cost
          	.50 per month until you delete them</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">After 1month
          	check your “In/Out Ratio” and delete the ones with lowest %</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">If you want to
          	received calls, use the Forwarding option by clicking the pen</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Also you can
          	add a note for the Caller ID by clicking the pen</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">This Caller ID
          	note will appear when you edit a campaign</p></li>
          </ul>
          <p STYLE="margin-bottom: 0in; line-height: 100%"><br/>
          </p>
          <p align="center" STYLE="margin-bottom: 0in; line-height: 100%; font-size: 1.25rem;">Lists</p>
          <ul>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Your list
          	should be in CSV format</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">The list file
          	does not need a head, but if you have one its ok</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">The phone
          	number should be in 1<sup>st</sup> column A</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Phone numbers
          	should be in 10 digits format and do not need a 1 in front</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Make sure there
          	are no spaces and characters in your list like these ( )  – /</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Here is an
          	example of a good phone number 9498587877</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Here is an
          	example of a bad phone number (949) 858-7877</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">If you would
          	like to personalize each outgoing message, leave info in the 2<sup>nd</sup>
          	column B</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Personalize
          	messages is called POPULATE, leave FirstName or business name in the
          	2<sup>nd</sup> column B</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Upload your
          	leads on the 2<sup>nd</sup> Tab left side of screen: “Lists”</p></li>
          </ul>
          <p STYLE="margin-bottom: 0in; line-height: 100%"><br/>
          </p>
          <p align="center" STYLE="margin-bottom: 0in; line-height: 100%; font-size: 1.25rem;">Campaigns</p>
          <ul>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Once you have
          	Caller IDs purchased and a List uploaded, you can make a campaign</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">“Campaigns”
          	is the 1<sup>st</sup> tab left of screen</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Click ADD NEW
          	CAMPAIGN, add several campaigns to send faster</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Add several
          	campaigns to send out different messages</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Enter a
          	Campaign Name, this can be edited anytime</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Type a message
          	160 characters or less</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Select the
          	Caller IDs to be used in this campaign</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Click Add to
          	save, and there will be a Date saved</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">You must
          	attached a list to a campaign by clicking the 3<sup>rd</sup> Action
          	button</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Once a campaign
          	is saved, see the Actions: Start, Edit, Attach List, Send Test
          	Message, Delete</p></li>
          </ul>
          <p STYLE="margin-bottom: 0in; line-height: 100%"><br/>
          </p>
          <p align="center" STYLE="margin-bottom: 0in; line-height: 100%; font-size: 1.25rem;">Delivery</p>
          <ul>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Message
          	blocking is a major challenge running SMS campaign but we have
          	solutions</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Make many campaigns that will send to the same list, each campaign has a different message</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Continue to add campaigns with different messages</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Use a campaign once a week because message blocking is temporary</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Once you have many campaigns, use different campaigns every day</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">During the day, every 2 hours: edit the message slightly changing a word or 2</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">For even better results, use caller IDs every other day</p></li>
          	<li><p STYLE="margin-bottom: 0in; line-height: 100%">Buy 50 caller IDs for Mon, Wed, Friday and buy 50 for Tues, Thurs, Saturday</p></li>
          </ul>
        </DialogContent>

        <DialogActions>
          <Button onClick={(e) => this.props.handleClose()} color="primary">
            Close
          </Button>
        </DialogActions>
      </Dialog>
    )
  }
}

export default SendingTipsLogic;
